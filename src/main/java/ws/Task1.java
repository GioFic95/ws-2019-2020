package ws;

import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.*;

import org.jgrapht.io.ImportException;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;
import ws.weights.PageRankWeight;
import ws.weights.SimpleWeight;
import ws.weights.Weight;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Class used to perform all the jobs related to the first task.
 */
public class Task1 {

    /**
     * Enum used to select which scoring one wants to use for the scoring.
     */
    private enum Scoring {
        CLUSTERING_COEFFICIENT, BETWEENNESS_CENTRALITY, CLOSENESS_CENTRALITY, ALPHA_CENTRALITY, PAGE_RANK
    }

    /**
     * Make several trials with different centrality measures and different "weights", by calling
     * {@link #computeScoring(Graph, Scoring, Weight, double, double, int)} with different parameters.
     * @see Weight and its sublasses.
     * @throws URISyntaxException if there are problems reading an input graph or creating an output file.
     * @throws IOException if there are problems reading an input graph or creating an output file.
     * @throws ImportException if there are problems reading an input graph.
     */
    public static void tryMeasures() throws URISyntaxException, IOException, ImportException {
        StringBuilder sbCCoeff = new StringBuilder();
        StringBuilder sbCc = new StringBuilder();
        StringBuilder sbBc = new StringBuilder();
        StringBuilder sbAc = new StringBuilder();
        StringBuilder sbPr = new StringBuilder();
        StringBuilder sbCCoeffW = new StringBuilder();
        StringBuilder sbCCoeffPr = new StringBuilder();
        StringBuilder sbCCoeffPrW = new StringBuilder();
        StringBuilder sbCCW = new StringBuilder();
        StringBuilder sbCCPr = new StringBuilder();
        StringBuilder sbCCPrW = new StringBuilder();

        for (int i=2000; i<=2018; i++) {
            for (int k : new int[]{5, 10, 20, 100}) {
                String year = String.valueOf(i);
                Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
                File dot = Utils.getNewFile("graphs/ds1", year, "dot");

                // clustering coefficient
                List<String> topCCoeff = computeScoring(graph, Scoring.CLUSTERING_COEFFICIENT, k);
                Utils.print(topCCoeff);
                GraphUtils.writeImage(dot, "plots/ccoeff", year + "_" + k, topCCoeff);
                sbCCoeff.append(year + "\t" + k + "\t" + topCCoeff + "\n");

                // betweenness centrality
                List<String> topBc = computeScoring(graph, Scoring.BETWEENNESS_CENTRALITY, k);
                Utils.print(topBc);
                GraphUtils.writeImage(dot, "plots/bc", year + "_" + k, topBc);
                sbBc.append(year + "\t" + k + "\t" + topBc + "\n");

                // closeness centrality
                List<String> topCc = computeScoring(graph, Scoring.CLOSENESS_CENTRALITY, k);
                Utils.print(topCc);
                GraphUtils.writeImage(dot, "plots/cc", year + "_" + k, topCc);
                sbCc.append(year + "\t" + k + "\t" + topCc + "\n");

                // alpha centrality
                List<String> topAc = computeScoring(graph, Scoring.ALPHA_CENTRALITY, k);
                Utils.print(topAc);
                GraphUtils.writeImage(dot, "plots/ac", year + "_" + k, topAc);
                sbAc.append(year + "\t" + k + "\t" + topAc + "\n");

                // page rank
                List<String> topPr = computeScoring(graph, Scoring.PAGE_RANK, k);
                Utils.print(topPr);
                GraphUtils.writeImage(dot, "plots/pr", year + "_" + k, topPr);
                sbPr.append(year + "\t" + k + "\t" + topPr + "\n");

                // weighted clustering coefficient
                Weight<MyVertex, MyEdgeDS1> weight = new SimpleWeight(graph);
                List<String> topCCoeffW = computeScoring(graph, Scoring.CLUSTERING_COEFFICIENT, weight, 0.9, 0.1, k);
                Utils.print(topCCoeffW);
                GraphUtils.writeImage(dot, "plots/ccoeffw", year + "_" + k, topCCoeffW);
                sbCCoeffW.append(year + "\t" + k + "\t" + topCCoeffW + "\n");

                // clustering coefficient weighted with page rank of the authors
                Weight<MyVertex, MyEdgeDS1> weightPr = new PageRankWeight(graph, year);
                List<String> topCCoeffPr = computeScoring(graph, Scoring.CLUSTERING_COEFFICIENT, weightPr, 0.5, 0.5, k);
                Utils.print(topCCoeffPr);
                GraphUtils.writeImage(dot, "plots/ccoeffpr", year + "_" + k, topCCoeffPr);
                sbCCoeffPr.append(year + "\t" + k + "\t" + topCCoeffPr + "\n");

                // clustering coefficient weighted with page rank of the authors and number of occurrences of the pair of keywords
                Weight weightPrW = Weight.compose(new SimpleWeight(graph), new PageRankWeight(graph, year), 0.5, 0.5);
                List<String> topCCoeffPrW = computeScoring(graph, Scoring.CLUSTERING_COEFFICIENT, weightPrW, 0.5, 0.5, k);
                Utils.print(topCCoeffPrW);
                GraphUtils.writeImage(dot, "plots/ccoeffprw", year + "_" + k, topCCoeffPrW);
                sbCCoeffPrW.append(year + "\t" + k + "\t" + topCCoeffPrW + "\n");

                // weighted closeness centrality
                Weight<MyVertex, MyEdgeDS1> weightCC = new SimpleWeight(graph);
                List<String> topCCW = computeScoring(graph, Scoring.CLOSENESS_CENTRALITY, weightCC, 0.9, 0.1, k);
                Utils.print(topCCW);
                GraphUtils.writeImage(dot, "plots/ccw", year + "_" + k, topCCW);
                sbCCW.append(year + "\t" + k + "\t" + topCCW + "\n");

                // closeness centrality weighted with page rank of the authors
                Weight<MyVertex, MyEdgeDS1> weightCCPr = new PageRankWeight(graph, year);
                List<String> topCCPr = computeScoring(graph, Scoring.CLOSENESS_CENTRALITY, weightCCPr, 0.5, 0.5, k);
                Utils.print(topCCPr);
                GraphUtils.writeImage(dot, "plots/ccpr", year + "_" + k, topCCPr);
                sbCCPr.append(year + "\t" + k + "\t" + topCCPr + "\n");

                // closeness centrality weighted with page rank of the authors and number of occurrences of the pair of keywords
                Weight weightCCPrW = Weight.compose(new SimpleWeight(graph), new PageRankWeight(graph, year), 0.5, 0.5);
                List<String> topCCPrW = computeScoring(graph, Scoring.CLOSENESS_CENTRALITY, weightCCPrW, 0.5, 0.5, k);
                Utils.print(topCCPrW);
                GraphUtils.writeImage(dot, "plots/ccprw", year + "_" + k, topCCPrW);
                sbCCPrW.append(year + "\t" + k + "\t" + topCCPrW + "\n");
            }
        }

        Utils.writeLog(sbCCoeff,"clust_coeff");
        Utils.writeLog(sbBc,"between_centr");
        Utils.writeLog(sbCc,"close_centr");
        Utils.writeLog(sbAc,"alpha_centr");
        Utils.writeLog(sbPr, "page_rank");
        Utils.writeLog(sbCCoeffW,"clust_coeff_weighed");
        Utils.writeLog(sbCCoeffPr,"clust_coeff_pr");
        Utils.writeLog(sbCCoeffPrW,"clust_coeff_pr_weighed");
        Utils.writeLog(sbCCW,"close_centr_weighed");
        Utils.writeLog(sbCCPr,"close_centr_pr");
        Utils.writeLog(sbCCPrW,"close_centr_pr_weighed");
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
    private static List<String> computeScoring(
            Graph<MyVertex, MyEdgeDS1> graph, Scoring scoring, Weight<MyVertex, MyEdgeDS1> weight, double a, double b, int k) {
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
     * A shortcut for {@link #computeScoring(Graph, Scoring, Weight, double, double, int)} with weight=null, a=0, b=0.
     * @param graph   The input graph, on which to compute the desired scoring.
     * @param scoring The scoring measure to be used.
     * @param k       How many nodes to pick as the "top ones".
     * @return        The list of the ids of the top k nodes, according to the specified scoring and weight.
     */
    private static List<String> computeScoring(Graph<MyVertex, MyEdgeDS1> graph, Scoring scoring, int k) {
        return computeScoring(graph, scoring, null, 0, 0, k);
    }
}
