package ws.myGraph;

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
}
