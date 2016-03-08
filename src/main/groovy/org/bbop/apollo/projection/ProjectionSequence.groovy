package org.bbop.apollo.projection
/**
 * Created by nathandunn on 9/24/15.
 */
class ProjectionSequence implements Comparable<ProjectionSequence> {

    String id
    String name
    String organism

    Integer order  // what order this should be processed as
    Integer offset = 0  // projected offset from originalOffset
    Integer originalOffset = 0 // original incoming coordinates . .  0 implies order = 0, >0 implies that order > 0
    List<String> features// a list of Features  // default is a single entry ALL . . if empty then all
    Integer unprojectedLength = 0
    // the length of the sequence before projection . . the projected length comes from the associated discontinuous projection


    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ProjectionSequence that = (ProjectionSequence) o

        if (name != that.name) return false

        return true
    }

    int hashCode() {
        int result
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }

    @Override
    int compareTo(ProjectionSequence o) {
        def test = name <=> o.name
        return test
//        }

//        organism <=> o.organism
    }

}
