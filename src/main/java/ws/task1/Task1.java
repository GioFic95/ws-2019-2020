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
import ws.task1.diffusionModels.IndependentCascade;
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
        Map<String, StringBuilder> sbs = new HashMap<>();

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
                    name = scoring.toString() + "_simple";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    top = Scoring.computeScoring(graph, year, name, scoring, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        Utils.writeLog(sb, name);
                    }

                    // metric weighted with SimpleWeight (based on number of papers using that keyword)
                    name = scoring.toString() + "_weighted";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    Weight<MyVertex, MyEdgeDS1> weight = new SimpleWeight(graph, year, name);
                    top = Scoring.computeScoring(graph, year, name, scoring, weight, 0.5, 0.5, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        Utils.writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors
                    name = scoring.toString() + "_pr";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    Weight<MyVertex, MyEdgeDS1> weightPr = new PageRankWeight(graph, year, name);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPr, 0.5, 0.5, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        Utils.writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with equal weights
                    name = scoring.toString() + "_prw";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    Weight weightPrW = Weight.compose(new SimpleWeight(graph, year, name), new PageRankWeight(graph, year, name), 0.5, 0.5);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPrW, 0.5, 0.5, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        Utils.writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, multipled
                    name = scoring.toString() + "_prw_mul";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    top = Scoring.computeScoringMul(graph, year, name, scoring, weightPrW, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        Utils.writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with different weights
                    name = scoring.toString() + "_prw_unb";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    Weight weightPrWUnb = Weight.compose(new SimpleWeight(graph, year, name), new PageRankWeight(graph, year, name), 0.3, 0.7);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPrWUnb, 0.7, 0.3, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        Utils.writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with different weights, multiplied
                    name = scoring.toString() + "_prw_unb_mul";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    top = Scoring.computeScoringMul(graph, year, name, scoring, weightPrWUnb, k);
                    Utils.print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        Utils.writeLog(sb, name);
                    }
                }
            }
        }
    }

    public static void spreadInfluence(String pattern) throws ImportException, IOException, URISyntaxException {
        String logPath = "logs/" + Utils.findLastLog(pattern);
        Utils.print("spread influence path: " + logPath);
        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "k", "seeds"}, logPath);
        StringBuilder sb = new StringBuilder();

        for (Record row : ir) {
            String year = row.getString("year");
            List<String> seeds = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(row.getString("seeds"));
            for (int i=0; i<jsonArray.length(); i++) {
                seeds.add(jsonArray.get(i).toString());
            }
            sb.append(year).append("\t").append(seeds.size()).append("\t").append(seeds).append("\n");
            Utils.print("\nyear: " + year + ", seeds: " + seeds.size() + " - " + seeds);

            Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);

//            Map<String, List<String>> independentCascade =
//                    new IndependentCascade(graph, seeds, SpreadingOfInfluence.getEdgePropagationProbabilities(graph, year)).propagate();
//            Utils.print("Independent Cascade: " + independentCascade);
//            sb.append(independentCascade+"\n");

            Map<MyEdgeDS1, Double> probabilities = DiffusionUtils.getEdgePropagationProbabilities(graph, year);
            IndependentCascade independentCascade = new IndependentCascade(graph, seeds, probabilities);
            Set<String> infected = Collections.emptySet();
            for (int i=0; i<4; i++) {
                infected = DiffusionUtils.allInfected(infected, independentCascade.iteration());
                Utils.print(infected);
                sb.append(i).append("\t").append(infected).append("\n");
            }
            sb.append("\n");
        }
        Utils.writeLog(sb, "independent_cascade");
    }
}
