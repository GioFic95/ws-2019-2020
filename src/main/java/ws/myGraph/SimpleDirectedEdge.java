package ws.myGraph;

import java.util.Objects;

/**
 * A class that describes an ordered pair of {@link MyVertex}.
 */
public class SimpleDirectedEdge {
    private MyVertex source;
    private MyVertex target;

    /**
     * To build an ordered pair, just give the source and the target.
     * @param source The first element of the pair.
     * @param dest   The second element of the pair.
     */
    public SimpleDirectedEdge(MyVertex source, MyVertex dest) {
        this.source = source;
        this.target = dest;
    }

    /**
     * Get the first element of the pair.
     * @return The first element of the pair.
     */
    public MyVertex getSource() {
        return source;
    }

    /**
     * Get the second element of the pair.
     * @return The second element of the pair.
     */
    public MyVertex getTarget() {
        return target;
    }

    /**
     * Builds a description of this pair.
     * @return A complete description of this pair.
     */
    @Override
    public String toString() {
        return "SimpleDirectedEdge(" + source + "; " + target + ")";
    }

    /**
     * Compare two pairs and decides that they are equal if they have the same source and the same target.
     * @param o A pair to be compared with this one.
     * @return True if the two pairs have the same source and target.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleDirectedEdge edge = (SimpleDirectedEdge) o;
        return source.equals(edge.source) &&
                target.equals(edge.target);
    }

    /**
     * Computes the hash code of this pair based on its source and target.
     * @return The hash code of this pair.
     */
    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
