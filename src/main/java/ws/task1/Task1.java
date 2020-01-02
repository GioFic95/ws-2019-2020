package ws.task1;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import ws.myGraph.SimpleDirectedEdge;
import ws.task1.diffusionModels.IndependentCascade;
import ws.weights.PageRankWeight;
import ws.weights.SimpleWeight;
import ws.weights.Weight;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

        // Prepare csv log file with the header
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        String name = "ic_iterations__" + LocalDateTime.now().format(formatter);
        StringBuilder sb_iterations = new StringBuilder()
                .append("year").append("\t").append("currentIteration").append("\t").append("infectedNodes").append("\n");
        try {
            Utils.writeLog(sb_iterations, name, false);
        } catch (IOException | URISyntaxException e) {
            System.err.println("couldn't write independent cascade iterations log");
            e.printStackTrace();
        }

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
            Map<SimpleDirectedEdge, Double> probabilities = DiffusionUtils.getEdgePropagationProbabilities(graph, year);
            IndependentCascade independentCascade = new IndependentCascade(name, year, graph, seeds, probabilities);
            Set<String> infected = Collections.emptySet();
            while (true) {
                Set<String> newInfected = independentCascade.iteration();
                infected = DiffusionUtils.allInfected(infected, newInfected);
                Utils.print(infected);
                sb.append(infected).append("\n");
                if (newInfected.isEmpty()) {
                    break;
                }
            }
            sb.append("\n");
        }
        Utils.writeLog(sb, "independent_cascade");
    }

    public static void drawSpreadInfluence(String pattern) throws IOException, URISyntaxException {
        String logPath = "logs/" + Utils.findLastLog(pattern);
        Utils.print("spread influence path: " + logPath);
        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "currentIteration", "infectedNodes"}, logPath);
        Map<String, Set<String>> infectedNodesIds = new HashMap<>();
        int seedNum = 0;
        String prevYear = "";
        StringBuilder sb = new StringBuilder()
                .append("year").append("\t").append("numSeeds").append("\t").append("infectedNodes").append("\n");

        for (Record row : ir) {
            Integer iteration;
            try {
                iteration = row.getInt("currentIteration");
            } catch (NumberFormatException ex) {
                Utils.print(row);
                continue;
            }
            String year = row.getString("year");
            String infectedNodesJson = row.getString("infectedNodes");
            Type type = new TypeToken<Map<MyVertex, Set<MyVertex>>>(){}.getType();
            Map<MyVertex, Set<MyVertex>> infectedNodes = MyVertex.getGson().fromJson(infectedNodesJson, type);

            if (iteration == 1) {   // new cascade
                // If the map of node names isn't empty (this isn't the first cascade), draw the graph relative
                // to the previous cascade, that is terminated, before starting the new one
                if (! infectedNodesIds.isEmpty()) {
                    File file = Utils.getNewFile("graphs/ds1", prevYear, "dot");
                    String name = prevYear + "_" + seedNum;
                    GraphUtils.writeImage(file, "plots/ic", name, infectedNodesIds);
                    Utils.print("writing graph " + name);

                    // Add the previous cascade results to the log
                    sb.append(prevYear).append("\t").append(seedNum).append("\t").append(infectedNodesIds).append("\n");
                }

                // begin analysing the new cascade
                prevYear = year;
                seedNum = infectedNodes.size();

                infectedNodesIds = infectedNodes.entrySet().stream().collect(Collectors.toMap(
                        myVertexSetEntry -> myVertexSetEntry.getKey().getId(),
                        myVertexSetEntry -> myVertexSetEntry.getValue().stream().map(MyVertex::getId).collect(Collectors.toSet())
                ));

            } else {   // old cascade
                // add current infected nodes to the previous ones
                for (Map.Entry<MyVertex, Set<MyVertex>> entry : infectedNodes.entrySet()) {
                    String newSeed = entry.getKey().getId();
                    Set<String> newInfectedNeighbors = entry.getValue().stream().map(MyVertex::getId).collect(Collectors.toSet());

                    infectedNodesIds.forEach((seedId, infectedNeighborsIds) -> {
                        if (infectedNeighborsIds.contains(newSeed)) {
                            infectedNeighborsIds.addAll(newInfectedNeighbors);
                        }
                    });
                }
            }
        }
        // Write the last cascade plot and add the relative log
        File file = Utils.getNewFile("graphs/ds1", prevYear, "dot");
        String name = prevYear + "_" + seedNum;
        GraphUtils.writeImage(file, "plots/ic", name, infectedNodesIds);
        Utils.print("writing graph " + name);
        sb.append(prevYear).append("\t").append(seedNum).append("\t").append(infectedNodesIds).append("\n");

        // Write the independent cascade results log
        Utils.writeLog(sb, "ic_results");
    }
}
