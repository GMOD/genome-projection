package org.bbop.apollo.gwt.shared.projection;

/**
 * Created by nathandunn on 2/14/16.
 */
public class Coordinate implements Comparable<Coordinate> {
    private Integer min;
    private Integer max;
    private ProjectionSequence sequence;
//    String organism

    public Coordinate(Integer min, Integer max) {
        this(min, max, null);
    }

    public Coordinate(Integer min, Integer max, ProjectionSequence sequence) {
        this.min = min;
        this.max = max;
        this.sequence = sequence;
    }

//    @Override
//    int compareTo(Coordinate o) {
//        min <=> o.min ?: max <=> o.max
//    }


    @Override
    public int compareTo(Coordinate o) {
        if (!min.equals(o.min)) {
            return min - o.min;
        }
        if (!max.equals(o.max)) {
            return max - o.max;
        }
        if (!sequence.equals(o.sequence)) {
            return sequence.compareTo(o.sequence);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;

        Coordinate that = (Coordinate) o;

        if (min != that.min) return false;
        if (max != that.max) return false;
        if (!sequence.equals(that.sequence)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (min != null ? min.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Coordinate{" +
                "min=" + min +
                ", max=" + max +
                ", sequence=" + sequence +
//                organism ? ", organism=" + organism : "" +
                '}';
    }

    Boolean isValid() {
        return min >= 0 && max >= 0;
    }

    Integer getLength() {
        return Math.abs(max - min);
    }

    void addOffset(Integer offset) {
        min = min + offset;
        max = max + offset;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public ProjectionSequence getSequence() {
        return sequence;
    }
}
