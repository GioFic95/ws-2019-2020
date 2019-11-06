package ws.weights;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.NeighborCache;
import ws.Utils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link Weight} based on the number of occurrences of the keywords in the articles.
 */
public class SimpleWeight extends Weight<MyVertex, MyEdgeDS1> {

    /**
     * Create a new weighting of the given graph, based on the number of occurrences of the keywords in the articles.
     * @param graph The graph to be weighted.
     */
    public SimpleWeight(Graph<MyVertex, MyEdgeDS1> graph) {
        super(graph);
    }

    /**
     * Computes the weighting of the graph, by assigning to each node the normalized sum of the occurrences
     * of the keyword contained in the node into the articles that appears on the edges incident on that node.
     * @return A weighting of the graph based on the number of occurrences of the keywords in the articles.
     * @see MyEdgeDS1#getAuthors() MyEdgeDS1.getAuthors
     * @see MyVertex#getValue() MyVertex.getValue
     */
    @Override
    public Map<MyVertex, Double> getScores() {
        NeighborCache<MyVertex, MyEdgeDS1> neighborGraph = new NeighborCache<>(graph);
        Map<MyVertex, Double> map = new HashMap<>();
        for (MyVertex mv : graph.vertexSet()) {
            Set<MyVertex> neighbors = neighborGraph.neighborsOf(mv);
            double sum = 0;
            for (MyVertex mn : neighbors) {
                // To each edge of the the graph from DS1, assign a partial weight w, s.t. w = sum_{a in A}(n_a),
                // where A is the list of authors that use the pair of keywords represented by this edge,
                // n_a is the number of times a used this pair of keywords.
                MyEdgeDS1 e = graph.getEdge(mv, mn);
                sum += e.getAuthors().values().stream().reduce(0, Integer::sum);
            }
            // To each node of the the graph from DS1, assign a weight that is the sum of the partial weights of
            // the incident edges.
            map.put(mv, sum);
        }
        // Normalize with the max weight.
        double max = Collections.max(map.values());
        map = map.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> Math.log(entry.getValue())/Math.log(max)));
        Utils.print("SimpleWeight scores: " + map);
        return map;
    }

}
