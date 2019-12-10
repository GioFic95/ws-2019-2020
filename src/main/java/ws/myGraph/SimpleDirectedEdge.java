package ws.myGraph;

import java.util.Objects;

public class SimpleDirectedEdge {
    private MyVertex source;
    private MyVertex target;

    public SimpleDirectedEdge(MyVertex source, MyVertex dest) {
        this.source = source;
        this.target = dest;
    }

    public MyVertex getSource() {
        return source;
    }

    public MyVertex getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "SimpleDirectedEdge(" + source + "; " + target + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleDirectedEdge edge = (SimpleDirectedEdge) o;
        return source.equals(edge.source) &&
                target.equals(edge.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
