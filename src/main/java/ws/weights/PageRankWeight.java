package ws.weights;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.io.ImportException;
import ws.myGraph.GraphUtils;
import ws.utils.Utils;
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
     * @param name  The name of this weight.
     */
    public PageRankWeight(Graph<MyVertex, MyEdgeDS1> graph, String year, String name) {
        super(graph, name);
        this.year = year;
    }

    /**
     * Uses {@link GraphUtils#authorsPageRank(String)} to compute the {@link PageRank} of the authors that
     * appear on the edges of this graph, and, based on this score, produces the weighing of the graph, assigning to
     * each node the normalized sum of the scores of the authors that appears on the edges incident on that node.
     * @param normalize If the results have to be normalized.
     * @return A weighting of the graph based on {@link PageRank}.
     * @see MyEdgeDS1#getAuthors() MyEdgeDS1.getAuthors
     */
    public Map<MyVertex, Double> getScores(boolean normalize) {
        try {
            Map<MyVertex, Double> authorsPR = authorsPageRank(year);  // Compute the page rank on the corresponding graph in DS2.
            NeighborCache<MyVertex, MyEdgeDS1> neighborGraph = new NeighborCache<>(graph);
            Map<MyVertex, Double> map = new HashMap<>();
            for (MyVertex mv : graph.vertexSet()) {
                Set<MyVertex> neighbors = neighborGraph.neighborsOf(mv);
                double sum = 0;
                for (MyVertex mn : neighbors) {
                    // To each edge of the the graph from DS1, assign a partial weight w, s.t. w = sum_{a in A}(pr_a),
                    // where A is the list of authors that use the pair of keywords represented by that edge,
                    // pr_a is the PageRank score given to the author a in the graph of DS2 corresponding to the same
                    // year of this graph.
                    MyEdgeDS1 e = graph.getEdge(mv, mn);
                    Map<String, Integer> authors = e.getAuthors();
                    sum += authorsPR.entrySet().stream()
                            .filter(entry -> authors.containsKey(entry.getKey().getValue()))
                            .mapToDouble(Map.Entry::getValue).sum();
                }
                // To each node of the the graph from DS1, assign a weight that is the sum of the partial weights of
                // the incident edges.
                map.put(mv, sum);
            }
            Utils.print("partial pageranks" + map);

            if (normalize) {
                // Normalize with the max weight.
                double max = Collections.max(map.values());
                map = map.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey, entry -> {
                            double res = entry.getValue() / max;
                            assert res >= 0 && res <= 1 : "id " + entry.getKey().getId() + " " + entry.getValue() + "/" + max;
                            return res;
                        }));
            }
            Utils.writeLog(new StringBuilder(year + "\t" + map + "\n"), "page_rank_" + name, false);
            return map;
        } catch (ImportException | IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalStateException("Something went wrong while computing PageRankWeight scores\n" + e.getMessage());
        }
    }

    /**
     * Shortcut for the standard way of computing scores, i.e., with normalization.
     * @return the normalized scores.
     */
    @Override
    public Map<MyVertex, Double> getScores() {
        return getScores(true);
    }
}
