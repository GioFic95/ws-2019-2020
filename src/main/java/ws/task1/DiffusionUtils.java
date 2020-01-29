package ws.task1;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import org.jgrapht.Graph;
import org.jgrapht.io.ImportException;
import ws.Main;
import ws.Utils;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;
import ws.myGraph.SimpleDirectedEdge;
import ws.weights.PageRankWeight;
import ws.weights.SimpleWeight;
import ws.weights.Weight;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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
    public static Map<SimpleDirectedEdge, Double> getEdgePropagationProbabilities(Graph<MyVertex, MyEdgeDS1> graph, String year, double k)
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

//            Utils.print("num: " + num + ", denSource: " + denSource + ", denTarget: " + denTarget);
            sb.append("num: ").append(num).append(", denSource: ").append(denSource).append(", denTarget: ")
                    .append(denTarget).append(", ratioSource: ").append(num/denSource).append(", ratioTarget: ")
                    .append(num/denTarget).append("\n");
        }

        // Normalize and multiply by a constant factor 1.5
        double max = Collections.max(probabilities.values());
        probabilities = probabilities.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Math.min(1, k*entry.getValue()/max)));
        Utils.print("PropagationProbabilities: " + probabilities);
        // Utils.writeLog(sb, "PropagationProbabilities_" + year);
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

    /**
     * If {@param filename} is null or empty string, the last independent cascade unified log is used
     * (as produced by {@link Task1#writeUnifiedSpreadInfluence(String)}).
     * @param fileNamePattern todo
     * @param dirName todo
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void drawSpreadInfluence(String fileNamePattern, String dirName) throws URISyntaxException, IOException {
        // Prepare the output dir and file
        String pathName = dirName == null || dirName.equals("") ? "ic" : "ic_" + dirName;
        String fileName;
        File dir = new File(Main.class.getResource("plots").toURI().getPath() + "\\" + pathName);
        if (! dir.isDirectory()) {
            if (!dir.mkdir()) {
                Utils.print("the directory " + dirName + "does not exist and can't be created.");
            }
        }
        if (fileNamePattern == null || fileNamePattern.equals("")) {
            fileName = Utils.findLastLog("ic_results__.*\\.txt");
        } else {
            fileName = Utils.findLastLog(fileNamePattern);
        }

        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "numSeeds", "infectedNodes"}, "logs/" + fileName);
        Type type = new TypeToken<Map<MyVertex, Set<MyVertex>>>(){}.getType();
        for (Record row : ir) {
            String year = row.getString("year");
            String numSeeds = row.getString("numSeeds");
            String infectedNodesJson = row.getString("infectedNodes");
            Map<MyVertex, Set<MyVertex>> infectedNodes;
            try {
                infectedNodes = MyVertex.getGson().fromJson(infectedNodesJson, type);
            } catch (JsonSyntaxException ex) {
                Utils.print("Can't parse string '" + infectedNodesJson + "' as json.");
                continue;
            }
            Map<String, Set<String>> infectedNodesIds = infectedNodes.entrySet().stream().collect(Collectors.toMap(
                    myVertexSetEntry -> myVertexSetEntry.getKey().getId(),
                    myVertexSetEntry -> myVertexSetEntry.getValue().stream().map(MyVertex::getId).collect(Collectors.toSet())
            ));
            String outName = year + "_" + numSeeds;
            Utils.print("writing graph '" + outName + "' in dir 'plots/" + pathName + "'");
            File file = Utils.getNewFile("graphs/ds1", year, "dot");
            GraphUtils.writeImage(file, "plots/" + pathName, outName, infectedNodesIds);
        }
    }

    /**
     * If {@param filename} is null or empty string, the last independent cascade merge log is used
     * (as produced by {@link Task1#mergeSpreadInfluenceResults(int, double)}).
     * @param fileNamePattern todo
     * @param dirName todo
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void drawMerge(String fileNamePattern, String dirName) throws URISyntaxException, IOException {
        // Prepare the output dir and file
        String pathName = dirName == null || dirName.equals("") ? "ic" : "ic_" + dirName;
        String fileName;
        File dir = new File(Main.class.getResource("plots").toURI().getPath() + "\\" + pathName);
        if (! dir.isDirectory()) {
            if (!dir.mkdir()) {
                Utils.print("the directory " + dirName + "does not exist and can't be created.");
            }
        }
        if (fileNamePattern == null || fileNamePattern.equals("")) {
            fileName = Utils.findLastLog("ic_results_merged2__.*\\.txt");
        } else {
            fileName = Utils.findLastLog(fileNamePattern);
        }

        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "numSeeds", "infectedNodes"}, "logs/" + fileName);
        Type type = new TypeToken<Map<Set<MyVertex>, Set<MyVertex>>>(){}.getType();
        for (Record row : ir) {
            String year = row.getString("year");
            String numSeeds = row.getString("numSeeds");
            String infectedNodesJson = row.getString("infectedNodes");
            Map<Set<MyVertex>, Set<MyVertex>> infectedNodes;
            try {
                infectedNodes = MyVertex.getGson().fromJson(infectedNodesJson, type);
            } catch (JsonSyntaxException ex) {
                Utils.print("Can't parse string '" + infectedNodesJson + "' as json.");
                continue;
            }
            Map<Set<String>, Set<String>> infectedNodesIds = infectedNodes.entrySet().stream().collect(Collectors.toMap(
                    myVertexSetEntry -> myVertexSetEntry.getKey().stream().map(MyVertex::getId).collect(Collectors.toSet()),
                    myVertexSetEntry -> myVertexSetEntry.getValue().stream().map(MyVertex::getId).collect(Collectors.toSet())
            ));
            Utils.print("infectedNodesIds" + infectedNodesIds);
            String outName = year + "_" + numSeeds;
            Utils.print("writing graph '" + outName + "' in dir 'plots/" + pathName + "'");
            File file = Utils.getNewFile("graphs/ds1", year, "dot");
            GraphUtils.writeImage(file, "plots/" + pathName, outName, infectedNodesIds);
        }
    }
}
