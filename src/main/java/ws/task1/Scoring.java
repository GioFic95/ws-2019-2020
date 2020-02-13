package ws.task1;

import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.*;
import ws.utils.Utils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;
import ws.weights.Weight;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static ws.utils.Utils.writeLog;

/**
 * Class used to define and compute the scoring of the nodes of the graphs in DS1.
 */
public class Scoring {
    /**
     * Enum used to select which scoring one wants to use for the scoring.
     */
    enum ScoringMeasure {
        CLUSTERING_COEFFICIENT, BETWEENNESS_CENTRALITY, CLOSENESS_CENTRALITY, ALPHA_CENTRALITY, PAGE_RANK;

        /**
         * Compute a string representation of this metric.
         * @return A string representation of this metric.
         */
        @Override
        public String toString() {
            return name().substring(0, 3).toLowerCase();
        }
    }

    /**
     * Compute the list of the top k nodes, according to the specified scoring and weight.
     * @param graph   The input graph, on which to compute the desired scoring.
     * @param name    The name to be used for the log file.
     * @param year    The year of the given graph.
     * @param scoring The scoring measure to be used.
     * @param weight  The weight to be applied jointly with the scoring (effectively, this is another scoring measure).
     * @param a       The proportion in which the scoring is considered compared to the weight.
     * @param b       The proportion in which the weight is considered compared to the scoring.
     * @param k       How many nodes to pick as the "top ones".
     * @return        The list of the ids of the top k nodes, according to the specified scoring and weight.
     * @throws IOException if raised by {@link Utils#writeLog}.
     * @throws URISyntaxException if raised by {@link Utils#writeLog}.
     */
    static List<String> computeScoring(
            Graph<MyVertex, MyEdgeDS1> graph, String year, String name, ScoringMeasure scoring,
            Weight<MyVertex, MyEdgeDS1> weight, double a, double b, int k)
            throws IOException, URISyntaxException {
        StringBuilder sb = new StringBuilder(year);
        k = Integer.min(k, graph.vertexSet().size());
        final Map<MyVertex, Double> scores;

        switch (scoring) {
            case CLUSTERING_COEFFICIENT:
                scores = new HashMap<>(new ClusteringCoefficient<>(graph).getScores());
                break;
            case BETWEENNESS_CENTRALITY:
                scores = new HashMap<>(new BetweennessCentrality<>(graph).getScores());
                break;
            case CLOSENESS_CENTRALITY:
                scores = new HashMap<>(new HarmonicCentrality<>(graph).getScores());
                break;
            case ALPHA_CENTRALITY:
                scores = new HashMap<>(new AlphaCentrality<>(graph).getScores());
                break;
            case PAGE_RANK:
                scores = new HashMap<>(new PageRank<>(graph).getScores());
                break;
            default:
                throw new IllegalArgumentException("scoring not supported");
        }
        sb.append("\t").append(scores);

        // Normalize with the max score.
        final double max = Collections.max(scores.values());
        scores.forEach((myVertex, score) -> scores.put(myVertex, score / max));
        sb.append("\t").append(scores);

        if (weight != null) {
            Map<?, Double> weights = weight.getScores();
            sb.append("\t").append(weights);
            if (a!=0 || b!=0) {
                scores.forEach((myVertex, score) -> scores.put(myVertex, (score * a) + (weights.get(myVertex) * b)));
            } else {
                scores.forEach((myVertex, score) -> scores.put(myVertex, score * weights.get(myVertex)));
            }
            sb.append("\t").append(scores);
        }
        sb.append("\n");

        writeLog(sb, "scoring_" + name, false);

        List<String> topK = new ArrayList<>();
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(scores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            scores.remove(topVertex);
            topK.add(topVertex.getId());
        }
        return topK;
    }

    /**
     * A shortcut for {@link #computeScoring(Graph, String, String, ScoringMeasure, Weight, double, double, int)} with weight=null, a=0, b=0.
     * @param graph   The input graph, on which to compute the desired scoring.
     * @param name    The name to be used for the log file.
     * @param year    The year of the given graph.
     * @param scoring The scoring measure to be used.
     * @param k       How many nodes to pick as the "top ones".
     * @return        The list of the ids of the top k nodes, according to the specified scoring and weight.
     * @throws IOException if raised by {@link #computeScoring(Graph, String, String, ScoringMeasure, Weight, double, double, int)}
     * @throws URISyntaxException if raised by {@link #computeScoring(Graph, String, String, ScoringMeasure, Weight, double, double, int)}
     */
    static List<String> computeScoring(Graph<MyVertex, MyEdgeDS1> graph, String year, String name, ScoringMeasure scoring, int k)
            throws IOException, URISyntaxException {
        return computeScoring(graph, year, name, scoring, null, 0, 0, k);
    }

    /**
     * A shortcut for {@link #computeScoring(Graph, String, String, ScoringMeasure, Weight, double, double, int)} with weight=null, a=0, b=0.
     * @param graph   The input graph, on which to compute the desired scoring.
     * @param name    The name to be used for the log file.
     * @param year    The year of the given graph.
     * @param scoring The scoring measure to be used.
     * @param weight  The weight to be applied jointly with the scoring.
     * @param k       How many nodes to pick as the "top ones".
     * @return        The list of the ids of the top k nodes, according to the specified scoring and weight.
     * @throws IOException if raised by {@link #computeScoring(Graph, String, String, ScoringMeasure, Weight, double, double, int)}.
     * @throws URISyntaxException if raised by {@link #computeScoring(Graph, String, String, ScoringMeasure, Weight, double, double, int)}.
     */
    static List<String> computeScoringMul(Graph<MyVertex, MyEdgeDS1> graph, String year, String name,
                                          ScoringMeasure scoring, Weight<MyVertex, MyEdgeDS1> weight, int k)
            throws IOException, URISyntaxException {
        return computeScoring(graph, year, name, scoring, weight, 0, 0, k);
    }
}
