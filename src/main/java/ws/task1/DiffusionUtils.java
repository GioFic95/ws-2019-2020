package ws.task1;

import org.jgrapht.Graph;
import org.jgrapht.io.ImportException;
import ws.Utils;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;
import ws.myGraph.SimpleDirectedEdge;
import ws.weights.PageRankWeight;
import ws.weights.SimpleWeight;
import ws.weights.Weight;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class DiffusionUtils {

    /**
     * To each edge of the the graph from DS1, assign a weight w, s.t. w = sum_{a in A}(pr_a * n_a),
     * where A is the list of authors that use the pair of keywords represented by this edge,
     * pr_a is the PageRank score given to the author a in the graph of DS2 corresponding to the same year of this graph,
     * n_a is the number of times a used this pair of keywords.
     * @param graph todo
     * @param year  todo
     * @return todo
     * @throws ImportException todo
     * @throws IOException todo
     * @throws URISyntaxException todo
     * @see <a href="https://stackoverflow.com/questions/5969447/java-random-integer-with-non-uniform-distribution" target="_blank">Java: random integer with non-uniform distribution</a>
     */
    public static Map<SimpleDirectedEdge, Double> getEdgePropagationProbabilities(Graph<MyVertex, MyEdgeDS1> graph, String year)
            throws ImportException, IOException, URISyntaxException {
        Map<MyVertex, Double> authorsPR = GraphUtils.authorsPageRank(year);
        Map<MyVertex, Double> nodesWeights = Weight.compose(
                new SimpleWeight(graph, year, "PropagationProbabilities"),
                new PageRankWeight(graph, year, "PropagationProbabilities"), 0.5, 0.5).getScores();
        Map<SimpleDirectedEdge, Double> probabilities = new HashMap<>();

        // To each edge of the the graph from DS1 I assign a weight w, s.t. w = sum_{a in A}(pr_a * n_a),
        // where A is the list of authors that use the pair of keywords represented by this edge,
        // pr_a is the PageRank score given to the author a in the graph of DS2 corresponding to the same year of this graph,
        // n_a is the number of times a used this pair of keywords.
        for (MyEdgeDS1 myEdge : graph.edgeSet()) {
            MyVertex source = (MyVertex) myEdge.getSource();
            MyVertex target = (MyVertex) myEdge.getTarget();
            Map<String, Integer> authors = myEdge.getAuthors();

            double num = authorsPR.entrySet().stream()
                    .filter(entry -> authors.containsKey(entry.getKey().getValue()))
                    .mapToDouble(entry -> entry.getValue() * authors.get(entry.getKey().getValue()))
                    .sum();
            double denSource = nodesWeights.get(source);
            double denTarget = nodesWeights.get(target);
            probabilities.put(new SimpleDirectedEdge(source, target), num/denSource);
            probabilities.put(new SimpleDirectedEdge(target, source), num/denTarget);
        }
        double max = Collections.max(probabilities.values());
        probabilities = probabilities.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()/max));
        Utils.print("PropagationProbabilities: " + probabilities);
        return probabilities;
    }

    /**
     * todo
     * @param s1
     * @param s2
     * @return
     */
    public static Set<String> allInfected(Set<String> s1, Set<String> s2) {
        Set<String> s = new HashSet<>(s1);
        s.addAll(s2);
        return s;
    }
}
