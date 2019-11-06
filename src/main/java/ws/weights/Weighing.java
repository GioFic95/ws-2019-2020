package ws.weights;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.io.ImportException;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Function;

public class Weighing {

    public static Graph<MyVertex, MyEdgeDS1> addPageRank(Graph<MyVertex, MyEdgeDS1> graph, String year)
            throws ImportException, IOException, URISyntaxException {

        Map<MyVertex, Double> authorsPR = GraphUtils.authorsPageRank(year);

        // To each edge of the the graph from DS1 I assign a weight w, s.t. w = sum_{a in A}(pr_a),
        // where A is the list of authors that use the pair of keywords represented by this edge,
        // pr_a is the PageRank score given to the author a in the graph of DS2 corresponding to the same year of this graph.
        Function<MyEdgeDS1, Double> f = myEdge -> {
            Map<String, Integer> authors = myEdge.getAuthors();
            return (Double) authorsPR.entrySet().stream()
                    .filter(entry -> authors.containsKey(entry.getKey().getValue()))
                    .mapToDouble(Map.Entry::getValue).sum();
        };

        return new AsWeightedGraph<>(graph, f, true, false);
    }

    public static Graph<MyVertex, MyEdgeDS1> addWeightedPageRank(Graph<MyVertex, MyEdgeDS1> graph, String year)
            throws ImportException, IOException, URISyntaxException {

        Map<MyVertex, Double> authorsPR = GraphUtils.authorsPageRank(year);

        // To each edge of the the graph from DS1 I assign a weight w, s.t. w = sum_{a in A}(pr_a * n_a),
        // where A is the list of authors that use the pair of keywords represented by this edge,
        // pr_a is the PageRank score given to the author a in the graph of DS2 corresponding to the same year of this graph,
        // n_a is the number of times a used this pair of keywords.
        Function<MyEdgeDS1, Double> f = myEdge -> {
            Map<String, Integer> authors = myEdge.getAuthors();
            return (Double) authorsPR.entrySet().stream()
                    .filter(entry -> authors.containsKey(entry.getKey().getValue()))
                    .mapToDouble(entry -> entry.getValue() * authors.get(entry.getKey().getValue())).sum();
        };
        return new AsWeightedGraph<>(graph, f, true, false);
    }

    /**
     * Assign to each edge a weight that is the number of occurrences of the pair of keywords represented by that edge
     * @param graph
     * @return
     */
    public static Graph<MyVertex, MyEdgeDS1> addSimpleWeights(Graph<MyVertex, MyEdgeDS1> graph) {
        Function<MyEdgeDS1, Double> f = myEdge -> myEdge.getAuthors().values().stream().reduce(0, Integer::sum).doubleValue();
        return new AsWeightedGraph<>(graph, f, true, false);
    }
}
