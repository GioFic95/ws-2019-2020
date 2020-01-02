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
     * For each edge {u, v} of the undirected graph taken from DS1, compute a pair of probabilities pr_uv and pr_vu,
     * respectively for the directed edges (u, v) and (v, u), where:
     * - pr_uv = num/den_uv e pv_vu = num/den_vu,
     * - num = sum_{a in A}(n_a), where A is the list of authors that use the pair of keywords represented by the edge,
     *         {u, v} and n_a is the number of times a used this pair of keywords,
     * - den_uv = sum_{for all {x, v} in E(G)}(sum_{a in A}(n_a)),
     * - den_vu = sum_{for all {x, u} in E(G)}(sum_{a in A}(n_a)).
     * Finally the probabilities are normalized and multiplied by a constant factor 1.2
     *
     * @param graph todo
     * @param year  todo
     * @return todo
     * @throws ImportException todo
     * @throws IOException todo
     * @throws URISyntaxException todo
     * @see <a href="https://stackoverflow.com/questions/5969447/java-random-integer-with-non-uniform-distribution" target="_blank">Java: random integer with non-uniform distribution</a>
     */
    public static Map<SimpleDirectedEdge, Double> getEdgePropagationProbabilities(Graph<MyVertex, MyEdgeDS1> graph, String year)
            throws IOException, URISyntaxException {
        StringBuilder sb = new StringBuilder();

        Map<MyVertex, Double> nodesWeights = new SimpleWeight(graph, year, "PropagationProbabilities").getScores(false);
        Map<SimpleDirectedEdge, Double> probabilities = new HashMap<>();

        for (MyEdgeDS1 myEdge : graph.edgeSet()) {
            MyVertex source = (MyVertex) myEdge.getSource();
            MyVertex target = (MyVertex) myEdge.getTarget();

            double num = myEdge.getAuthors().values().stream().reduce(0, Integer::sum).doubleValue();
            double denSource = nodesWeights.get(target);
            double denTarget = nodesWeights.get(source);
            probabilities.put(new SimpleDirectedEdge(source, target), num/denSource);
            probabilities.put(new SimpleDirectedEdge(target, source), num/denTarget);

            Utils.print("num: " + num + ", denSource: " + denSource + ", denTarget: " + denTarget);
            sb.append("num: ").append(num).append(", denSource: ").append(denSource).append(", denTarget: ")
                    .append(denTarget).append(", ratioSource: ").append(num/denSource).append(", ratioTarget: ")
                    .append(num/denTarget).append("\n");
        }

        // Normalize and multiply by a constant factor 1.2
        double max = Collections.max(probabilities.values());
        probabilities = probabilities.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> 1.5*entry.getValue()/max));
        Utils.print("PropagationProbabilities: " + probabilities);
        Utils.writeLog(sb, "PropagationProbabilities_" + year);
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
