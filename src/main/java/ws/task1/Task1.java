package ws.task1;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import org.jgrapht.Graph;

import org.jgrapht.io.ImportException;
import org.json.JSONArray;
import ws.Utils;
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
     * Make several trials with different centrality measures and different "weights", by calling
     * {@link Scoring#computeScoring(Graph, String, String, Scoring.ScoringMeasure, Weight, double, double, int)} with different parameters.
     * @see Weight and its sublasses.
     * @throws URISyntaxException if there are problems reading an input graph or creating an output file.
     * @throws IOException if there are problems reading an input graph or creating an output file.
     * @throws ImportException if there are problems reading an input graph.
     */
    public static void tryMeasures() throws URISyntaxException, IOException, ImportException {
        // delete logs about scoring
        Utils.delMatchigFiles("logs", "(scoring|simple_weight|page_rank)[a-zA-Z_]*\\.txt");

//        StringBuilder sbCCoeff = new StringBuilder();
//        StringBuilder sbCc = new StringBuilder();
//        StringBuilder sbBc = new StringBuilder();
//        StringBuilder sbAc = new StringBuilder();
//        StringBuilder sbPr = new StringBuilder();
//        StringBuilder sbCCoeffW = new StringBuilder();
//        StringBuilder sbCCoeffPr = new StringBuilder();
//        StringBuilder sbCCoeffPrW = new StringBuilder();
//        StringBuilder sbCCoeffPrWUnb = new StringBuilder();
        StringBuilder sbCCoeffPrWMul = new StringBuilder();
        StringBuilder sbCCoeffPrWUnbMul = new StringBuilder();
//        StringBuilder sbCCW = new StringBuilder();
//        StringBuilder sbCCPr = new StringBuilder();
//        StringBuilder sbCCPrW = new StringBuilder();
//        StringBuilder sbCCPrWUnb = new StringBuilder();

        for (int i=2000; i<=2018; i++) {
            for (int k : new int[]{5, 10, 20, 100}) {
                String year = String.valueOf(i);
                Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
                File dot = Utils.getNewFile("graphs/ds1", year, "dot");
/*
                // clustering coefficient
                List<String> topCCoeff = Scoring.computeScoring(graph, year, "ccoeff", Scoring.ScoringMeasure.CLUSTERING_COEFFICIENT, k);
                Utils.print(topCCoeff);
                GraphUtils.writeImage(dot, "plots/ccoeff", year + "_" + k, topCCoeff);
                sbCCoeff.append(year + "\t" + k + "\t" + topCCoeff + "\n");

                // betweenness centrality
                List<String> topBc = Scoring.computeScoring(graph, year, "bc", Scoring.ScoringMeasure.BETWEENNESS_CENTRALITY, k);
                Utils.print(topBc);
                GraphUtils.writeImage(dot, "plots/bc", year + "_" + k, topBc);
                sbBc.append(year + "\t" + k + "\t" + topBc + "\n");

                // closeness centrality
                List<String> topCc = Scoring.computeScoring(graph, year, "cc", Scoring.ScoringMeasure.CLOSENESS_CENTRALITY, k);
                Utils.print(topCc);
                GraphUtils.writeImage(dot, "plots/cc", year + "_" + k, topCc);
                sbCc.append(year + "\t" + k + "\t" + topCc + "\n");

                // alpha centrality
                List<String> topAc = Scoring.computeScoring(graph, year, "ac", Scoring.ScoringMeasure.ALPHA_CENTRALITY, k);
                Utils.print(topAc);
                GraphUtils.writeImage(dot, "plots/ac", year + "_" + k, topAc);
                sbAc.append(year + "\t" + k + "\t" + topAc + "\n");

                // page rank
                List<String> topPr = Scoring.computeScoring(graph, year, "pr", Scoring.ScoringMeasure.PAGE_RANK, k);
                Utils.print(topPr);
                GraphUtils.writeImage(dot, "plots/pr", year + "_" + k, topPr);
                sbPr.append(year + "\t" + k + "\t" + topPr + "\n");

                // weighted clustering coefficient
                Weight<MyVertex, MyEdgeDS1> weight = new SimpleWeight(graph, year, "ccoeffw");
                List<String> topCCoeffW = Scoring.computeScoring(graph, year, "ccoeffw", Scoring.ScoringMeasure.CLUSTERING_COEFFICIENT, weight, 0.9, 0.1, k);
                Utils.print(topCCoeffW);
                GraphUtils.writeImage(dot, "plots/ccoeffw", year + "_" + k, topCCoeffW);
                sbCCoeffW.append(year + "\t" + k + "\t" + topCCoeffW + "\n");

                // clustering coefficient weighted with page rank of the authors
                Weight<MyVertex, MyEdgeDS1> weightPr = new PageRankWeight(graph, year, "ccoeffpr");
                List<String> topCCoeffPr = Scoring.computeScoring(graph, year, "ccoeffpr", Scoring.ScoringMeasure.CLUSTERING_COEFFICIENT, weightPr, 0.5, 0.5, k);
                Utils.print(topCCoeffPr);
                GraphUtils.writeImage(dot, "plots/ccoeffpr", year + "_" + k, topCCoeffPr);
                sbCCoeffPr.append(year + "\t" + k + "\t" + topCCoeffPr + "\n");
*/
                // clustering coefficient weighted with page rank of the authors and number of occurrences of the pair of keywords
                Weight weightPrW = Weight.compose(new SimpleWeight(graph, year, "ccoeffprw"), new PageRankWeight(graph, year, "ccoeffprw"), 0.5, 0.5);
//                List<String> topCCoeffPrW = Scoring.computeScoring(graph, year, "ccoeffprw", Scoring.ScoringMeasure.CLUSTERING_COEFFICIENT, weightPrW, 0.5, 0.5, k);
//                Utils.print(topCCoeffPrW);
//                GraphUtils.writeImage(dot, "plots/ccoeffprw", year + "_" + k, topCCoeffPrW);
//                sbCCoeffPrW.append(year + "\t" + k + "\t" + topCCoeffPrW + "\n");

                List<String> topCCoeffPrWMul = Scoring.computeScoringMul(graph, year, "ccoeffprwmul", Scoring.ScoringMeasure.CLUSTERING_COEFFICIENT, weightPrW, k);
                Utils.print(topCCoeffPrWMul);
                GraphUtils.writeImage(dot, "plots/ccoeffprwmul", year + "_" + k, topCCoeffPrWMul);
                sbCCoeffPrWMul.append(year + "\t" + k + "\t" + topCCoeffPrWMul + "\n");

                // clustering coefficient weighted with page rank of the authors and number of occurrences of the pair of keywords (unbalanced)
                Weight weightPrWUnb = Weight.compose(new SimpleWeight(graph, year, "ccoeffprwunb"), new PageRankWeight(graph, year, "ccoeffprwunb"), 0.3, 0.7);
//                List<String> topCCoeffPrWUnb = Scoring.computeScoring(graph, year, "ccoeffprwunb", Scoring.ScoringMeasure.CLUSTERING_COEFFICIENT, weightPrWUnb, 0.7, 0.3, k);
//                Utils.print(topCCoeffPrWUnb);
//                GraphUtils.writeImage(dot, "plots/ccoeffprwunb", year + "_" + k, topCCoeffPrWUnb);
//                sbCCoeffPrWUnb.append(year + "\t" + k + "\t" + topCCoeffPrWUnb + "\n");

                List<String> topCCoeffPrWUnbMul = Scoring.computeScoringMul(graph, year, "ccoeffprwunbmul", Scoring.ScoringMeasure.CLUSTERING_COEFFICIENT, weightPrWUnb, k);
                Utils.print(topCCoeffPrWUnbMul);
                GraphUtils.writeImage(dot, "plots/ccoeffprwunbmul", year + "_" + k, topCCoeffPrWUnbMul);
                sbCCoeffPrWUnbMul.append(year + "\t" + k + "\t" + topCCoeffPrWUnbMul + "\n");

/*
                // weighted closeness centrality
                Weight<MyVertex, MyEdgeDS1> weightCC = new SimpleWeight(graph, year, "ccw");
                List<String> topCCW = Scoring.computeScoring(graph, year, "ccw", Scoring.ScoringMeasure.CLOSENESS_CENTRALITY, weightCC, 0.9, 0.1, k);
                Utils.print(topCCW);
                GraphUtils.writeImage(dot, "plots/ccw", year + "_" + k, topCCW);
                sbCCW.append(year + "\t" + k + "\t" + topCCW + "\n");

                // closeness centrality weighted with page rank of the authors
                Weight<MyVertex, MyEdgeDS1> weightCCPr = new PageRankWeight(graph, year, "ccpr");
                List<String> topCCPr = Scoring.computeScoring(graph, year, "ccpr", Scoring.ScoringMeasure.CLOSENESS_CENTRALITY, weightCCPr, 0.5, 0.5, k);
                Utils.print(topCCPr);
                GraphUtils.writeImage(dot, "plots/ccpr", year + "_" + k, topCCPr);
                sbCCPr.append(year + "\t" + k + "\t" + topCCPr + "\n");

                // closeness centrality weighted with page rank of the authors and number of occurrences of the pair of keywords
                Weight weightCCPrW = Weight.compose(new SimpleWeight(graph, year, "ccprw"), new PageRankWeight(graph, year, "ccprw"), 0.5, 0.5);
                List<String> topCCPrW = Scoring.computeScoring(graph, year, "ccprw", Scoring.ScoringMeasure.CLOSENESS_CENTRALITY, weightCCPrW, 0.5, 0.5, k);
                Utils.print(topCCPrW);
                GraphUtils.writeImage(dot, "plots/ccprw", year + "_" + k, topCCPrW);
                sbCCPrW.append(year + "\t" + k + "\t" + topCCPrW + "\n");

                // closeness centrality weighted with page rank of the authors and number of occurrences of the pair of keywords (unbalanced)
                Weight weightCCPrWUnb = Weight.compose(new SimpleWeight(graph, year, "ccprwunb"), new PageRankWeight(graph, year, "ccprwunb"), 0.3, 0.7);
                List<String> topCCPrWUnb = Scoring.computeScoring(graph, year, "ccprwunb", Scoring.ScoringMeasure.CLOSENESS_CENTRALITY, weightCCPrWUnb, 0.7, 0.3, k);
                Utils.print(topCCPrWUnb);
                GraphUtils.writeImage(dot, "plots/ccprwunb", year + "_" + k, topCCPrWUnb);
                sbCCPrWUnb.append(year + "\t" + k + "\t" + topCCPrWUnb + "\n");*/
            }
        }

//        Utils.writeLog(sbCCoeff,"clust_coeff");
//        Utils.writeLog(sbBc,"between_centr");
//        Utils.writeLog(sbCc,"close_centr");
//        Utils.writeLog(sbAc,"alpha_centr");
//        Utils.writeLog(sbPr, "page_rank");
//        Utils.writeLog(sbCCoeffW,"clust_coeff_weighed");
//        Utils.writeLog(sbCCoeffPr,"clust_coeff_pr");
//        Utils.writeLog(sbCCoeffPrW,"clust_coeff_pr_weighed");
//        Utils.writeLog(sbCCoeffPrWUnb,"clust_coeff_pr_weighed_unb");
        Utils.writeLog(sbCCoeffPrWMul,"clust_coeff_pr_weighed_mul");
        Utils.writeLog(sbCCoeffPrWUnbMul,"clust_coeff_pr_weighed_unb_mul");
//        Utils.writeLog(sbCCW,"close_centr_weighed");
//        Utils.writeLog(sbCCPr,"close_centr_pr");
//        Utils.writeLog(sbCCPrW,"close_centr_pr_weighed");
//        Utils.writeLog(sbCCPrWUnb,"close_centr_pr_weighed_unb");
    }

    public static void spreadInfluence(String logPath) throws ImportException, IOException, URISyntaxException {
        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "k", "seeds"}, logPath);
        StringBuilder sb = new StringBuilder();

        for (Record row : ir) {
            String year = row.getString("year");
            List<String> seeds = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(row.getString("seeds"));
            for (int i=0; i<jsonArray.length(); i++) {
                seeds.add(jsonArray.get(i).toString());
            }
            sb.append(year).append("\t").append(seeds).append("\t");
            Utils.print("year: " + year + ", seeds: " + seeds);

            Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
            Map<String, List<String>> independentCascade = SpreadingOfInfluence.independentCascade(graph, year, seeds);
            Utils.print("Independent Cascade: " + independentCascade);
            sb.append(independentCascade+"\n");
        }
        Utils.writeLog(sb, "independent_cascade");
    }
}
