package ws.task1;

import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.*;
import ws.Utils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;
import ws.weights.Weight;

import java.util.*;

/**
 * Class used to define and compute the scoring of the nodes of the graphs in DS1.
 */
public class Scoring {
    /**
     * Enum used to select which scoring one wants to use for the scoring.
     */
    enum ScoringMeasure {
        CLUSTERING_COEFFICIENT, BETWEENNESS_CENTRALITY, CLOSENESS_CENTRALITY, ALPHA_CENTRALITY, PAGE_RANK
    }

    /**
     * Compute the list of the top k nodes, according to the specified scoring and weight.
     * @param graph   The input graph, on which to compute the desired scoring.
     * @param scoring The scoring measure to be used.
     * @param weight  The weight to be applied jointly with the scoring (effectively, this is another scoring measure).
     * @param a       The proportion in which the scoring is considered compared to the weight.
     * @param b       The proportion in which the weight is considered compared to the scoring.
     * @param k       How many nodes to pick as the "top ones".
     * @return        The list of the ids of the top k nodes, according to the specified scoring and weight.
     */
    static List<String> computeScoring(
            Graph<MyVertex, MyEdgeDS1> graph, ScoringMeasure scoring, Weight<MyVertex, MyEdgeDS1> weight, double a, double b, int k) {
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
                scores = new HashMap<>(new ClosenessCentrality<>(graph).getScores());
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
        Utils.print("scores" + scores);
        List<String> topK = new ArrayList<>();
        if (weight != null) {
            Map<?, Double> weights = weight.getScores();
            Utils.print("weights" + weights);
            scores.forEach((myVertex, score) -> scores.put(myVertex, (score * a) + (weights.get(myVertex) * b)));
            Utils.print("scores" + scores);
        }
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(scores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            scores.remove(topVertex);
            topK.add(topVertex.getId());
        }
        return topK;
    }

    /**
     * A shortcut for {@link #computeScoring(Graph, ScoringMeasure, Weight, double, double, int)} with weight=null, a=0, b=0.
     * @param graph   The input graph, on which to compute the desired scoring.
     * @param scoring The scoring measure to be used.
     * @param k       How many nodes to pick as the "top ones".
     * @return        The list of the ids of the top k nodes, according to the specified scoring and weight.
     */
    static List<String> computeScoring(Graph<MyVertex, MyEdgeDS1> graph, ScoringMeasure scoring, int k) {
        return computeScoring(graph, scoring, null, 0, 0, k);
    }
}
