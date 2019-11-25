package ws.task1.diffusionModels;

import org.jgrapht.Graph;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DiffusionModel {
    protected Graph<MyVertex, MyEdgeDS1> graph;
    protected Map<MyVertex, NodeStatus> statuses;
    protected long currentIteration = 0;
    protected List<String> seeds;

    public enum NodeStatus {SUSCEPTIBLE, INFECTED, REMOVED}

    public DiffusionModel(Graph<MyVertex, MyEdgeDS1> graph, List<String> seeds) {
        this.graph = graph;
        this.seeds = seeds;
        this.statuses = new HashMap<>();
        for (MyVertex mv : graph.vertexSet()) {
            if (seeds.contains(mv.getId())) {
                statuses.put(mv, NodeStatus.INFECTED);
            } else {
                statuses.put(mv, NodeStatus.SUSCEPTIBLE);
            }
        }
    }

    public abstract Set<String> iteration();
}
