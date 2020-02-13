package ws.task1.diffusionModels;

import org.jgrapht.Graph;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class that represent a generic diffusion model.
 */
public abstract class DiffusionModel {
    protected Graph<MyVertex, MyEdgeDS1> graph;
    protected Map<MyVertex, NodeStatus> statuses;
    protected long currentIteration = 0;
    protected List<String> seeds;

    public enum NodeStatus {SUSCEPTIBLE, INFECTED, REMOVED}

    /**
     * Build the diffusion model.
     * @param graph The graph on which the model has to be applied.
     * @param seeds The set of nodes from which the diffusion begins.
     */
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

    /**
     * Simulate an iteration of the model.
     * @return The set of nodes that became infected in this iteration.
     */
    public abstract Set<String> iteration();
}
