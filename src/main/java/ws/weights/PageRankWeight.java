package ws.weights;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.io.ImportException;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ws.myGraph.GraphUtils.authorsPageRank;

/**
 * A {@link Weight} based on {@link PageRank}.
 */
public class PageRankWeight extends Weight<MyVertex, MyEdgeDS1> {
    private String year;

    /**
     * Create a new weighting of the given graph, based on {@link PageRank}.
     * @param graph The graph to be weighted.
     * @param year  The year of the graph.
     */
    public PageRankWeight(Graph<MyVertex, MyEdgeDS1> graph, String year) {
        super(graph);
        this.year = year;
    }

    /**
     * Uses {@link ws.myGraph.GraphUtils#authorsPageRank(String)} to compute the {@link PageRank} of the authors that
     * appear on the edges of this graph, and, based on this score, produces the weighing of the graph, assigning to
     * each node the normalized sum of the scores of the authors that appears on the edges incident on that node.
     * @return A weighting of the graph based on {@link PageRank}.
     * @see MyEdgeDS1#getAuthors() MyEdgeDS1.getAuthors
     */
    @Override
    public Map<MyVertex, Double> getScores() {
        try {
            Map<MyVertex, Double> authorsPR = authorsPageRank(year);
            NeighborCache<MyVertex, MyEdgeDS1> neighborGraph = new NeighborCache<>(graph);
            Map<MyVertex, Double> map = new HashMap<>();
            for (MyVertex mv : graph.vertexSet()) {
                Set<MyVertex> neighbors = neighborGraph.neighborsOf(mv);
                double sum = 0;
                for (MyVertex mn : neighbors) {
                    MyEdgeDS1 e = graph.getEdge(mv, mn);
                    Map<String, Integer> authors = e.getAuthors();
                    sum += authorsPR.entrySet().stream()
                            .filter(entry -> authors.containsKey(entry.getKey().getValue()))
                            .mapToDouble(Map.Entry::getValue).sum();
                }
                map.put(mv, sum);
            }
            double max = Collections.max(map.values());
            map = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()/max));
            return map;
        } catch (ImportException | IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalStateException("Something went wrong while computing authors' PageRank\n" + e.getMessage());
        }
    }

}
