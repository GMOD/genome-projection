package org.bbop.apollo.gwt.shared.projection;

import com.google.common.base.Joiner;

import java.util.*;

/**
 * Created by nathandunn on 2/22/16.
 */
public class MultiSequenceProjection extends AbstractProjection{

//    // if a projection includes multiple sequences, this will include greater than one
    TreeMap<ProjectionSequence, DiscontinuousProjection> sequenceDiscontinuousProjectionMap = new TreeMap<>();
    Integer padding = 0 ;

//    ProjectionDescription projectionDescription  // description of how this is generated
//
//    List<String> chunks = new ArrayList<>()
//    ProjectionChunkList projectionChunkList = new ProjectionChunkList()

    @Override
    public Integer projectValue(Integer input) {
        ProjectionSequence projectionSequence = getProjectionSequence(input);
        if (projectionSequence==null) {
            return UNMAPPED_VALUE;
        }
        DiscontinuousProjection discontinuousProjection = sequenceDiscontinuousProjectionMap.get(projectionSequence);
        // TODO: buffer for scaffolds is currently 1 . . the order;
        Integer returnValue = discontinuousProjection.projectValue(input - projectionSequence.getOriginalOffset());
        if (returnValue == UNMAPPED_VALUE) {
            return UNMAPPED_VALUE;
        } else {
            return returnValue + projectionSequence.getOffset();
        }
    }

    @Override
    public Integer projectReverseValue(Integer input) {
        ProjectionSequence projectionSequence = getReverseProjectionSequence(input);
        if (projectionSequence==null) return -1;
//        return sequenceDiscontinuousProjectionMap.get(projectionSequence).projectReverseValue(input - projectionSequence.offset) + projectionSequence.originalOffset
        // TODO: we are using order as a buffer
        return sequenceDiscontinuousProjectionMap.get(projectionSequence).projectReverseValue(input - projectionSequence.getOffset());
    }

    @Override
    public Integer getLength() {
        Map.Entry<ProjectionSequence, DiscontinuousProjection> entry = sequenceDiscontinuousProjectionMap.lastEntry();
        return entry.getKey().getOffset()+ entry.getValue().getLength();
    }

    @Override
    public String projectSequence(String inputSequence, Integer minCoordinate, Integer maxCoordinate, Integer offset) {
        Integer index = 0;
        List<String> sequenceList = new ArrayList<>();

        Set<ProjectionSequence> keySet =  sequenceDiscontinuousProjectionMap.keySet();
        List<ProjectionSequence> sortedKeys = new ArrayList<>(keySet);
        Collections.sort(sortedKeys, new Comparator<ProjectionSequence>() {
            @Override
            public int compare(ProjectionSequence o1, ProjectionSequence o2) {
                return o1.getOrder()-o2.getOrder();
            }
        });

        // we start at the very bottom and go up
        for(ProjectionSequence projectionSequence : sortedKeys ){
            DiscontinuousProjection discontinuousProjection = sequenceDiscontinuousProjectionMap.get(projectionSequence);
            Integer sequenceLength = projectionSequence.getUnprojectedLength();
//            Integer projectedLength = discontinuousProjection.getLength();
            offset = index;

            // case 5: no overlap
            if(index > maxCoordinate || index+sequenceLength  < minCoordinate){
                // do nothing
                System.out.println("doing nothing with ${index}-${index+sequenceLength} in ${minCoordinate}-${maxCoordinate}");
            }
            // case 3: inbetween
            else
            if(minCoordinate > index && maxCoordinate < index + sequenceLength){
                sequenceList.add(discontinuousProjection.projectSequence(inputSequence,minCoordinate-index+offset, maxCoordinate - index+offset,offset));
            }
            // case 1: right edge
            else
            if(minCoordinate > index && maxCoordinate >= index + sequenceLength){
                sequenceList.add(discontinuousProjection.projectSequence(inputSequence,minCoordinate-index+offset, sequenceLength+offset,offset));
            }
            // case 2: left edge
            else
            if(minCoordinate <= index  && maxCoordinate < sequenceLength+index){
                sequenceList.add(discontinuousProjection.projectSequence(inputSequence,offset, maxCoordinate - index+offset,offset));
            }
            // case 4: overlap / all
            else
            if(minCoordinate <= index && maxCoordinate >= index + sequenceLength){
                sequenceList.add(discontinuousProjection.projectSequence(inputSequence,offset, sequenceLength+offset,offset));
            }
//            else{
//                throw new RuntimeException("Should not get here: ${minCoordinate},${maxCoordinate}")
//            }
            index += sequenceLength;
        }

        // not really used .  .. .  but otherwise would carve up into different bits
        return Joiner.on("").skipNulls().join(sequenceList);
    }

    /**
     * Find which sequence I am on by iterating over coordinates
     * @param input
     * @return
     */
    ProjectionSequence getProjectionSequence(Integer input) {

        Integer offset = 0;

        for (ProjectionSequence projectionSequence : getOrderedProjectionSequence()) {
            DiscontinuousProjection projection = sequenceDiscontinuousProjectionMap.get(projectionSequence);
            if (input >= offset && input <= projection.getOriginalLength() + offset) {
                return projectionSequence;
            }
            offset += projection.getOriginalLength();
        }
        return null;
    }

    List<ProjectionSequence> getOrderedProjectionSequence(){
        Set<ProjectionSequence> keySet =  sequenceDiscontinuousProjectionMap.keySet();
        List<ProjectionSequence> sortedKeys = new ArrayList<>(keySet);
        Collections.sort(sortedKeys, new Comparator<ProjectionSequence>() {
            @Override
            public int compare(ProjectionSequence o1, ProjectionSequence o2) {
                return o1.getOrder()-o2.getOrder();
            }
        });
        return sortedKeys;
    }

    ProjectionSequence getReverseProjectionSequence(Integer input) {

        for (ProjectionSequence projectionSequence : getOrderedProjectionSequence()) {
            Integer bufferedLength = sequenceDiscontinuousProjectionMap.get(projectionSequence).getBufferedLength();
            if (input >= projectionSequence.getOffset() && input <= projectionSequence.getOffset() + bufferedLength) {
                return projectionSequence;
            }
        }
        return null;
    }

    Coordinate addInterval(int min, int max, ProjectionSequence sequence) {
        Coordinate coordinate = new Coordinate(min, max, sequence);
        return addCoordinate(coordinate);
    }

    Coordinate addCoordinate(Coordinate coordinate) {
        // if a single projection . . the default .. then assert that it is the same sequence / projection
        Integer padding = 0 ;
        DiscontinuousProjection discontinuousProjection = sequenceDiscontinuousProjectionMap.get(coordinate.getSequence());
        if (discontinuousProjection!=null) {
            discontinuousProjection.addInterval(coordinate.getMin(), coordinate.getMax(), padding);
        } else {
//        if (!projectionSequence) {
            ProjectionSequence internalProjectionSequence = coordinate.getSequence();

            Integer order = findSequenceOrderByName(internalProjectionSequence);
            internalProjectionSequence.setOrder(order);

            DiscontinuousProjection thisDiscontinuousProjection = new DiscontinuousProjection();
            thisDiscontinuousProjection.addInterval(coordinate.getMin(), coordinate.getMax(), padding);
            sequenceDiscontinuousProjectionMap.put(internalProjectionSequence, thisDiscontinuousProjection);
        }
        return coordinate ;
    }

    Integer findSequenceOrderByName(ProjectionSequence projectionSequence) {
        int index = 0;
        for (ProjectionSequence projectionSequence1 : getOrderedProjectionSequence()) {
            if (projectionSequence1.getName().equals(projectionSequence.getName())) {
                return index;
            }
            ++index;
        }
        return -1;
    }

}
