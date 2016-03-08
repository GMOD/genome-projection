package org.bbop.apollo.gwt.shared.projection;

import java.util.List;

/**
 * Created by nathandunn on 2/22/16.
 */
public class ProjectionSequence implements Comparable<org.bbop.apollo.gwt.shared.projection.ProjectionSequence> {

    private String id;
    private String name;
    private String organism;
    private Integer order; // what order this should be processed as
    private Integer offset = 0;  // projected offset from originalOffset
    private Integer originalOffset = 0; // original incoming coordinates . .  0 implies order = 0, >0 implies that order > 0
    private List<String> features; // a list of Features  // default is a single entry ALL . . if empty then all
    private Integer unprojectedLength = 0;  // the length of the sequence before projection . . the projected length comes from the associated discontinuous projection

    public int hashCode() {
        int result;
        result = 31  + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ProjectionSequence o) {
        return name.compareTo(o.getName());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOriginalOffset() {
        return originalOffset;
    }

    public void setOriginalOffset(Integer originalOffset) {
        this.originalOffset = originalOffset;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public Integer getUnprojectedLength() {
        return unprojectedLength;
    }

    public void setUnprojectedLength(Integer unprojectedLength) {
        this.unprojectedLength = unprojectedLength;
    }


}
