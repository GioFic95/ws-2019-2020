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
        // delete old logs about scoring
        Utils.delMatchigFiles("logs", "(scoring|simple_weight|page_rank)[a-zA-Z_]*\\.txt");

        for (int i=2000; i<=2018; i++) {
            for (int k : new int[]{5, 10, 20, 100}) {
                String year = String.valueOf(i);
                Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
                File dot = Utils.getNewFile("graphs/ds1", year, "dot");
                String name;
                List<String> top;
                StringBuilder sb;

                for (Scoring.ScoringMeasure scoring : Scoring.ScoringMeasure.values()) {
                    // simple metric
                    sb = new StringBuilder();
                    name = scoring.toString() + "_simple";
                    top = Scoring.computeScoring(graph, year, name, scoring, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    Utils.writeLog(sb, name);

                    // metric weighted with SimpleWeight (based on number of papers using that keyword)
                    sb = new StringBuilder();
                    name = scoring.toString() + "_weighted";
                    Weight<MyVertex, MyEdgeDS1> weight = new SimpleWeight(graph, year, name);
                    top = Scoring.computeScoring(graph, year, name, scoring, weight, 0.5, 0.5, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    Utils.writeLog(sb, name);

                    // metric weighted with page rank of the authors
                    sb = new StringBuilder();
                    name = scoring.toString() + "_pr";
                    Weight<MyVertex, MyEdgeDS1> weightPr = new PageRankWeight(graph, year, name);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPr, 0.5, 0.5, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    Utils.writeLog(sb, name);

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with equal weights
                    sb = new StringBuilder();
                    name = scoring.toString() + "_prw";
                    Weight weightPrW = Weight.compose(new SimpleWeight(graph, year, name), new PageRankWeight(graph, year, name), 0.5, 0.5);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPrW, 0.5, 0.5, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    Utils.writeLog(sb, name);

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, multipled
                    sb = new StringBuilder();
                    name = scoring.toString() + "_prw_mul";
                    top = Scoring.computeScoringMul(graph, year, name, scoring, weightPrW, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    Utils.writeLog(sb, name);

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with different weights
                    sb = new StringBuilder();
                        name = scoring.toString() + "_prw_unb";
                    Weight weightPrWUnb = Weight.compose(new SimpleWeight(graph, year, name), new PageRankWeight(graph, year, name), 0.3, 0.7);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPrWUnb, 0.7, 0.3, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    Utils.writeLog(sb, name);

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with different weights, multiplied
                    sb = new StringBuilder();
                    name = scoring.toString() + "_prw_unb_mul";
                    top = Scoring.computeScoringMul(graph, year, name, scoring, weightPrWUnb, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    Utils.writeLog(sb, name);
                }
            }
        }
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
