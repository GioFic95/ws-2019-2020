package ws.weights;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.NeighborCache;
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

public class PageRankWeight extends Weight<MyVertex, MyEdgeDS1> {
    private String year;

    public PageRankWeight(Graph<MyVertex, MyEdgeDS1> graph, String year) {
        super(graph);
        this.year = year;
    }

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
