package ws.task1;

import com.google.gson.reflect.TypeToken;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.ResultIterator;
import com.univocity.parsers.common.record.Record;
import org.jgrapht.Graph;

import org.jgrapht.alg.util.UnorderedPair;
import org.jgrapht.io.ImportException;
import org.json.JSONArray;
import ws.utils.DiffusionUtils;
import ws.utils.Utils;
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
import java.util.stream.Collectors;

import static ws.utils.Utils.*;


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
        delMatchigFiles("logs", "(scoring|simple_weight|page_rank)[a-zA-Z_]*\\.txt");
        Map<String, StringBuilder> sbs = new HashMap<>();

        for (int i=2000; i<=2018; i++) {
            for (int k : new int[]{5, 10, 20, 100}) {
                String year = String.valueOf(i);
                Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
                File dot = getNewFile("graphs/ds1", year, "dot");
                String name;
                List<String> top;
                StringBuilder sb;

                for (Scoring.ScoringMeasure scoring : Scoring.ScoringMeasure.values()) {
                    // simple metric
                    name = scoring.toString() + "_simple";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    top = Scoring.computeScoring(graph, year, name, scoring, k);
                    print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        writeLog(sb, name);
                    }

                    // metric weighted with SimpleWeight (based on number of papers using that keyword)
                    name = scoring.toString() + "_weighted";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    Weight<MyVertex, MyEdgeDS1> weight = new SimpleWeight(graph, year, name);
                    top = Scoring.computeScoring(graph, year, name, scoring, weight, 0.5, 0.5, k);
                    print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors
                    name = scoring.toString() + "_pr";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    Weight<MyVertex, MyEdgeDS1> weightPr = new PageRankWeight(graph, year, name);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPr, 0.5, 0.5, k);
                    print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with equal weights
                    name = scoring.toString() + "_prw";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    Weight weightPrW = Weight.compose(new SimpleWeight(graph, year, name), new PageRankWeight(graph, year, name), 0.5, 0.5);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPrW, 0.5, 0.5, k);
                    print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, multipled
                    name = scoring.toString() + "_prw_mul";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    top = Scoring.computeScoringMul(graph, year, name, scoring, weightPrW, k);
                    print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with different weights
                    name = scoring.toString() + "_prw_unb";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    Weight weightPrWUnb = Weight.compose(new SimpleWeight(graph, year, name), new PageRankWeight(graph, year, name), 0.3, 0.7);
                    top = Scoring.computeScoring(graph, year, name, scoring, weightPrWUnb, 0.7, 0.3, k);
                    print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        writeLog(sb, name);
                    }

                    // metric weighted with page rank of the authors and number of occurrences of the keyword, with different weights, multiplied
                    name = scoring.toString() + "_prw_unb_mul";
                    sb = sbs.computeIfAbsent(name, s -> new StringBuilder());
                    top = Scoring.computeScoringMul(graph, year, name, scoring, weightPrWUnb, k);
                    print(top);
                    GraphUtils.writeImage(dot, "plots/"+name, year + "_" + k, top);
                    sb.append(year + "\t" + k + "\t" + top + "\n");
                    if (i == 2018 && k == 100) {
                        writeLog(sb, name);
                    }
                }
            }
        }
    }

    /**
     * Simulate the whole process of spread of influence based on Independent Cascade model
     * (implemented in {@link IndependentCascade}).
     * The process stops when there isn't any new infected node.
     * @param pattern The pattern to be used to find the log file with the information about the seeds.
     * @param k       The multiplication factor to be used in {@link DiffusionUtils#getEdgePropagationProbabilities}.
     * @throws ImportException if raised by {@link GraphUtils#loadDS1Graph}.
     * @throws IOException if raised by {@link GraphUtils#loadDS1Graph}, {@link Utils#writeLog},
     *              {@link DiffusionUtils#getEdgePropagationProbabilities}.
     * @throws URISyntaxException if raised by {@link GraphUtils#loadDS1Graph}, {@link Utils#writeLog},
     *              {@link Utils#findLastLog}, {@link DiffusionUtils#getEdgePropagationProbabilities}.
     */
    public static void spreadInfluence(String pattern, double k) throws ImportException, IOException, URISyntaxException {
        String logPath = "logs/" + findLastLog(pattern);
        print("spread influence path: " + logPath);
        IterableResult<Record, ParsingContext> ir = readTSV(new String[]{"year", "k", "seeds"}, logPath);
        StringBuilder sb = new StringBuilder();

        // Prepare csv log file with the header
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        String name = "ic_iterations__" + LocalDateTime.now().format(formatter);
        StringBuilder sb_iterations = new StringBuilder()
                .append("year").append("\t").append("currentIteration").append("\t").append("infectedNodes").append("\n");
        try {
            writeLog(sb_iterations, name, false);
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
            print("\nyear: " + year + ", seeds: " + seeds.size() + " - " + seeds);

            Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
            Map<SimpleDirectedEdge, Double> probabilities = DiffusionUtils.getEdgePropagationProbabilities(graph, year, k);
            IndependentCascade independentCascade = new IndependentCascade(name, year, graph, seeds, probabilities);
            Set<String> infected = Collections.emptySet();
            while (true) {
                Set<String> newInfected = independentCascade.iteration();
                infected = DiffusionUtils.allInfected(infected, newInfected);
                print(infected);
                sb.append(infected).append("\n");
                if (newInfected.isEmpty()) {
                    break;
                }
            }
            sb.append("\n");
        }
        writeLog(sb, "independent_cascade");
    }

    /**
     * Put all the iteration of a simulation of Independent Cascade together, to reconstruct the whole process.
     * @param pattern The pattern to be used to find the log file with the information about the Independent Cascade iterations.
     * @throws IOException if raised by {@link Utils#writeLog}.
     * @throws URISyntaxException if raised by {@link Utils#writeLog}, {@link Utils#findLastLog}.
     */
    public static void writeUnifiedSpreadInfluence(String pattern) throws IOException, URISyntaxException {
        String logPath = "logs/" + findLastLog(pattern);
        print("spread influence path: " + logPath);
        IterableResult<Record, ParsingContext> ir = readTSV(new String[]{"year", "currentIteration", "infectedNodes"}, logPath);
        Map<MyVertex, Set<MyVertex>> infectedNodesUnified = new HashMap<>();
        Type type = new TypeToken<Map<MyVertex, Set<MyVertex>>>(){}.getType();
        int seedNum = 0;
        String prevYear = "";
        StringBuilder sb = new StringBuilder()
                .append("year").append("\t").append("numSeeds").append("\t").append("infectedNodes").append("\n");

        for (Record row : ir) {
            Integer iteration;
            try {
                iteration = row.getInt("currentIteration");
            } catch (NumberFormatException ex) {
                print("Can't parse string '" + row.getString("currentIteration") + "' as Integer.");
                continue;
            }
            String year = row.getString("year");
            String infectedNodesJson = row.getString("infectedNodes");
            Map<MyVertex, Set<MyVertex>> infectedNodes = MyVertex.getGson().fromJson(infectedNodesJson, type);

            if (iteration == 1) {   // new cascade
                // If the map of node names isn't empty (this isn't the first cascade), draw the graph relative
                // to the previous cascade, that is terminated, before starting the new one
                if (! infectedNodesUnified.isEmpty()) {
                    // Add the previous cascade results to the log
                    String name = prevYear + "_" + seedNum;
                    print("writing graph " + name);
                    String infectedNodesUnifiedJson = MyVertex.getGson().toJson(infectedNodesUnified, type);
                    sb.append(prevYear).append("\t").append(seedNum).append("\t").append(infectedNodesUnifiedJson).append("\n");
                }

                // begin analysing the new cascade
                prevYear = year;
                seedNum = infectedNodes.size();
                infectedNodesUnified = infectedNodes;

            } else {   // old cascade
                // add current infected nodes to the previous ones
                for (Map.Entry<MyVertex, Set<MyVertex>> entry : infectedNodes.entrySet()) {
                    MyVertex newSeed = entry.getKey();
                    Set<MyVertex> newInfectedNeighbors = entry.getValue();

                    infectedNodesUnified.forEach((seedId, infectedNeighborsIds) -> {
                        if (infectedNeighborsIds.contains(newSeed)) {
                            infectedNeighborsIds.addAll(newInfectedNeighbors);
                        }
                    });
                }
            }
        }
        // Write the last cascade log
        String name = prevYear + "_" + seedNum;
        print("writing graph " + name);
        String infectedNodesUnifiedJson = MyVertex.getGson().toJson(infectedNodesUnified, type);
        sb.append(prevYear).append("\t").append(seedNum).append("\t").append(infectedNodesUnifiedJson).append("\n");

        // Write the independent cascade results log
        writeLog(sb, "ic_results");
    }

    /**
     * Merge the last n independent cascade results.
     * @param n         The number of independent cascade simulations to be merged.
     * @param ratio     A node is added to a topic if it is infected at least in n/ratio times in the simulations.
     * @param threshold Two topics are merged if their overlap coefficient is greater than threshold.
     * @throws URISyntaxException if raised by {@link Utils#writeLog}, {@link Utils#findLastLog}.
     * @throws IOException if raised by {@link Utils#writeLog}.
     * @see <a href="https://en.wikipedia.org/wiki/Overlap_coefficient" target="_blank">Overlap Coefficient</a>.
     */
    public static void mergeSpreadInfluenceResults(int n, double ratio, double threshold) throws URISyntaxException, IOException {
        Type type1 = new TypeToken<Map<MyVertex, Set<MyVertex>>>() {}.getType();
        Type type2 = new TypeToken<Map<Set<MyVertex>, Set<MyVertex>>>() {}.getType();
        List<String> fileNames = findLastLogs("ic_results__[0-9].*\\.txt", n);
        print("fileNames: " + fileNames);
        StringBuilder sb1 = new StringBuilder()
                .append("year").append("\t").append("numSeeds").append("\t").append("infectedNodes").append("\n");
        StringBuilder sb2 = new StringBuilder()
                .append("year").append("\t").append("numSeeds").append("\t").append("infectedNodes").append("\n");

        // Create a list of iterators over the selected log files
        List<ResultIterator<Record, ParsingContext>> iterators = new ArrayList<>();
        for (String fileName : fileNames) {
            ResultIterator<Record, ParsingContext> ir = readTSV(new String[]{"year", "numSeeds", "infectedNodes"}, "logs/" + fileName).iterator();
            iterators.add(ir);
        }

        // Throw away the first row, which contains the header of the tsv file
        iterators.forEach(Iterator::next);

        while (iterators.get(0).hasNext()) {
            Map<MyVertex, Set<MyVertex>> infectedNodesMerged = new HashMap<>();
            List<Record> currentRows = new ArrayList<>();
            iterators.forEach(it -> currentRows.add(it.next()));

            String year = currentRows.get(0).getString("year");
            String seedNum = currentRows.get(0).getString("numSeeds");
            List<Map<MyVertex, Set<MyVertex>>> infectedNodes = new ArrayList<>();
            currentRows.forEach(row -> infectedNodes.add(MyVertex.getGson().fromJson(row.getString("infectedNodes"), type1)));

            // *** PHASE 1: add to a topic each keyword that is infected at least in 1/ratio of the simulations ***
            Map<MyVertex, Map<MyVertex, Integer>> counter = new HashMap<>();
            infectedNodes.forEach(
                    myVertexSetMap -> myVertexSetMap.forEach(
                            (seed, infected) -> {
                                if (infected.isEmpty()) {
                                    counter.put(seed, Map.of());
                                } else {
                                    infected.forEach(
                                        node -> counter.merge(seed, Map.of(node, 1),
                                                (myVertexIntegerMap1, myVertexIntegerMap2) -> {
                                                    Map<MyVertex, Integer> map = new HashMap<>(myVertexIntegerMap1);
                                                    myVertexIntegerMap2.forEach((k, v) -> map.merge(k, v, Integer::sum));
                                                    return map;
                                                }));
                                }}));
            print("counter: " + counter);

            counter.forEach((k, v) -> infectedNodesMerged.put(k, v.entrySet().stream()
                    .filter(entry -> entry.getValue() >= n*ratio)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet())));

            // Add the current record to the output file, first phase of merge
            String name = year + "_" + seedNum;
            print("writing graph " + name + ", phase 1");
            String infectedNodesMergedJson = MyVertex.getGson().toJson(infectedNodesMerged, type1);
            sb1.append(year).append("\t").append(seedNum).append("\t").append(infectedNodesMergedJson).append("\n");

            // *** PHASE 2: merge similar topics ***
            // compute similarities
            Map<UnorderedPair<MyVertex, MyVertex>, Double> similarities = new HashMap<>();
            for (Map.Entry<MyVertex, Set<MyVertex>> entry1 : infectedNodesMerged.entrySet()) {
                for (Map.Entry<MyVertex, Set<MyVertex>> entry2 : infectedNodesMerged.entrySet()) {
                    UnorderedPair<MyVertex, MyVertex> pair = new UnorderedPair<>(entry1.getKey(), entry2.getKey());
                    if (!similarities.containsKey(pair)) {
                        // compute overlap coefficient
                        Set<MyVertex> intersect = new HashSet<>(entry1.getValue());
                        intersect.retainAll(entry2.getValue());
                        double sim = (double) intersect.size() / Math.min(entry1.getValue().size(), entry2.getValue().size());
                        similarities.put(pair, sim);
                    }
                }
            }

            // Create set of similar pairs of sets
            Set<UnorderedPair<MyVertex, MyVertex>> similarSets = similarities.entrySet().stream()
                    .filter(entry -> entry.getValue() > threshold)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            // creates groups of sets with high similarity
            Set<Set<MyVertex>> groups = new HashSet<>();
            Set<MyVertex> merged = new HashSet<>();
            for (UnorderedPair<MyVertex, MyVertex> set1 : similarSets) {
                MyVertex v1 = set1.getFirst();
                MyVertex v2 = set1.getSecond();
                Set<MyVertex> currGroup = new HashSet<>();

                Set<UnorderedPair<MyVertex, MyVertex>> tempSim = similarSets.stream()
                        .filter(entry -> entry.hasElement(v1) || entry.hasElement(v2))
                        .collect(Collectors.toSet());
                tempSim.forEach(pair -> {
                    currGroup.add(pair.getFirst());
                    currGroup.add(pair.getSecond());
                });

                groups.add(currGroup);
                merged.addAll(currGroup);
            }

            Map<Set<MyVertex>, Set<MyVertex>> infectedNodesMerged2 = new HashMap<>();
            groups.forEach(group -> {
                Set<MyVertex> seeds = new HashSet<>();
                Set<MyVertex> infected = new HashSet<>();
                group.forEach(mv -> {
                    seeds.add(mv);
                    infected.addAll(infectedNodesMerged.get(mv));
                });
                infectedNodesMerged2.put(seeds, infected);
            });
            infectedNodesMerged.forEach((k, v) -> {
                if (!merged.contains(k)) {
                    infectedNodesMerged2.put(Set.of(k), v);
                }
            });

            // Add the current record to the output file, second phase of merge
            print("writing graph " + name + ", phase 2");
            infectedNodesMergedJson = MyVertex.getGson().toJson(infectedNodesMerged2, type2);
            sb2.append(year).append("\t").append(seedNum).append("\t").append(infectedNodesMergedJson).append("\n");

            print("infectedNodes avg len: " + infectedNodes.stream().mapToInt(Map::size).average().getAsDouble() + ", counter len: " + counter.size() + ", infectedNodesMerged len: " + infectedNodesMerged.size() + ", infectedNodesMerged2 len: " + infectedNodesMerged2.size());
        }

        // Write the merged independent cascade log
        writeLog(sb1, "ic_results_merged1");
        writeLog(sb2, "ic_results_merged2");
    }

    /**
     * Run the whole flow of spread of influence based on {@link IndependentCascade}: simulate all the iterations,
     * put the iterations together, draw each iteration, repeat the process n times, then merge the resulting topics,
     * and finally draw the result.
     * @param n             The number of simulations to be performed and put together.
     * @param k             The multiplication factor to be used in {@link DiffusionUtils#getEdgePropagationProbabilities}.
     * @param ratio         The ratio to be used in {@link #mergeSpreadInfluenceResults}.
     * @param threshold     The threshold to be used in {@link #mergeSpreadInfluenceResults}.
     * @param doSimulations If true, execute the simulations, if false, previous results are used.
     * @param name          The name to be used in logs and plots.
     * @throws ImportException if raised by any of the invoked methods.
     * @throws IOException if raised by any of the invoked methods.
     * @throws URISyntaxException if raised by any of the invoked methods.
     */
    public static void multipleIndependentCascadeFlow(int n, double k, double ratio, double threshold, boolean doSimulations, String name)
            throws ImportException, IOException, URISyntaxException {
        if (doSimulations) {
            for (int i = 1; i <= n; i++) {
                spreadInfluence("alp_prw__.*\\.txt", k);
                writeUnifiedSpreadInfluence("ic_iterations__.*\\.txt");
                DiffusionUtils.drawSpreadInfluence("", name + i);
            }
        }
        mergeSpreadInfluenceResults(n, ratio, threshold);
        DiffusionUtils.drawSpreadInfluence("ic_results_merged1.*\\.txt", name + "merge1");   // phase 1
        DiffusionUtils.drawMerge("ic_results_merged2.*\\.txt", name + "merge2");             // phase 2
    }
}
