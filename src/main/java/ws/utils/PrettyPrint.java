package ws.utils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import org.jgrapht.io.ImportException;
import org.json.JSONArray;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyVertex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static ws.utils.Utils.findLastLog;
import static ws.utils.Utils.getNewFile;

/**
 * A class with utility functions for pretty printing the different kinds of output produced by the steps of the projects.
 */
public class PrettyPrint {
    final private static String outPath = "readable_output";

    /**
     * Print a summary of the top k nodes of each year, for each year in [2000, 2018] and for each k in {5, 10, 20, 100}.
     * @throws URISyntaxException if raised by {@link Utils#findLastLog}, {@link GraphUtils#getGraphMap},
     *              {@link #writeOutput}.
     * @throws IOException if raised by {@link GraphUtils#getGraphMap}, {@link #writeOutput}.
     * @throws ImportException if raised by {@link GraphUtils#getGraphMap}.
     */
    public static void topK() throws URISyntaxException, IOException, ImportException {
        StringBuilder sb = new StringBuilder("Top k nodes for each year\n");
        String topK = "logs/" + findLastLog("alp_prw__.*");
        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "k", "seeds"}, topK);
        String lastYear = "";

        for (Record row : ir) {
            String year = row.getString("year");
            if (!year.equals(lastYear)) {
                sb.append("\n\n*** " + year + " ***");
                lastYear = year;
            }

            String numSeeds = row.getString("k");
            JSONArray jsonArray = new JSONArray(row.getString("seeds"));
            if (numSeeds.equals(String.valueOf(jsonArray.length()))) {
                sb.append("\nTop " + numSeeds + ": ");
            } else {
                sb.append("\nTop " + numSeeds + " (" + jsonArray.length() + "): ");
            }

            Map<String, String> nodes = GraphUtils.getGraphMap(year);

            for (int i=0; i<jsonArray.length(); i++) {
                String seed = jsonArray.get(i).toString();
                if (i == jsonArray.length() -1) {
                    sb.append(nodes.get(seed) + ".");
                } else {
                    sb.append(nodes.get(seed) + ", ");
                }
            }
        }
        writeOutput(sb, "Top_K_Nodes");
    }

    /**
     * Print a summary of a simulation of spread of influence according to the Independent Cascade model,
     * for each year in [2000, 2018] and for each k in {5, 10, 20, 100}.
     * @throws URISyntaxException if raised by {@link Utils#findLastLog}, {@link #writeOutput}.
     * @throws IOException if raised by {@link #writeOutput}.
     */
    public static void spreadOfInfluence() throws URISyntaxException, IOException {
        Type type = new TypeToken<Map<MyVertex, Set<MyVertex>>>() {}.getType();
        String fileName = "logs/" + findLastLog("ic_results__[0-9].*\\.txt");
        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "numSeeds", "infectedNodes"}, fileName);
        StringBuilder sb = new StringBuilder("Simulation of spread of influence\n");
        String lastYear = "";
        int lastNumSeeds = -1;

        for (Record row : ir) {
            Map<MyVertex, Set<MyVertex>> infectedNodes;
            try {
                infectedNodes = MyVertex.getGson().fromJson(row.getString("infectedNodes"), type);
            } catch (JsonSyntaxException ex) {
                Utils.print("Can't parse string '" + row.getString("infectedNodes") + "' as json.");
                continue;
            }

            String year = row.getString("year");
            if (!year.equals(lastYear)) {
                sb.append("\n\n*** " + year + " ***");
                lastYear = year;
            }

            Integer numSeeds = row.getInt("numSeeds");
            if (20 < numSeeds && numSeeds < 100) {
                numSeeds = 100;
            } else if (10 < numSeeds && numSeeds < 20) {
                if (lastNumSeeds == 20) {
                    numSeeds = 100;
                } else {
                    numSeeds = 20;
                }
            }

            if (numSeeds == infectedNodes.size()) {
                sb.append("\n" + numSeeds + " seeds: ");
            } else {
                sb.append("\n" + numSeeds + " (" + infectedNodes.size() + ") seeds: ");
            }

            Map<String, Set<String>> infectedNodesText = infectedNodes.entrySet().stream().collect(Collectors.toMap(
                    entry -> entry.getKey().getValue(),
                    entry -> entry.getValue().stream().map(MyVertex::getValue).collect(Collectors.toSet())
            ));
            sb.append(infectedNodesText.toString() + ".");

            lastNumSeeds = numSeeds;
        }
        writeOutput(sb, "Spread_of_Influence");
    }

    /**
     * Print a summary of the result of the merge process,
     * for each year in [2000, 2018] and for each k in {5, 10, 20, 100}.
     * @throws URISyntaxException if raised by {@link Utils#findLastLog}, {@link #writeOutput}.
     * @throws IOException if raised by {@link #writeOutput}.
     */
    public static void merge() throws URISyntaxException, IOException {
        Type type = new TypeToken<Map<Set<MyVertex>, Set<MyVertex>>>() {}.getType();
        String fileName = "logs/" + findLastLog("ic_results_merged2__.*\\.txt");
        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "numSeeds", "infectedNodes"}, fileName);
        StringBuilder sb = new StringBuilder("Topics merge\n");
        String lastYear = "";
        int seedCounter = 0;
        String[] seeds = {"5", "10", "20", "100"};

        for (Record row : ir) {
            Map<Set<MyVertex>, Set<MyVertex>> infectedNodes;
            try {
                infectedNodes = MyVertex.getGson().fromJson(row.getString("infectedNodes"), type);
            } catch (JsonSyntaxException ex) {
                Utils.print("Can't parse string '" + row.getString("infectedNodes") + "' as json.");
                continue;
            }

            String year = row.getString("year");
            if (!year.equals(lastYear)) {
                sb.append("\n\n*** " + year + " ***");
                lastYear = year;
            }
            sb.append("\n" + seeds[seedCounter] + " seeds: ");

            Map<Set<String>, Set<String>> infectedNodesText = infectedNodes.entrySet().stream().collect(Collectors.toMap(
                    entry -> entry.getKey().stream().map(MyVertex::getValue).collect(Collectors.toSet()),
                    entry -> entry.getValue().stream().map(MyVertex::getValue).collect(Collectors.toSet())
            ));
            sb.append(infectedNodesText.toString() + ".");

            seedCounter = (seedCounter + 1 ) % seeds.length;
        }
        writeOutput(sb, "Topics_Merge");
    }

    /**
     * Print a summary of a simulation of spread of influence according to the Independent Cascade model,
     * for each year in [2000, 2018] and for each k in {5, 10, 20, 100}.
     * @throws URISyntaxException if raised by {@link Utils#findLastLog}, {@link Utils#getNewFile}.
     * @throws IOException if raised by {@link Files#copy}.
     */
    public static void tracing() throws URISyntaxException, IOException {
        String lastTracingPath = findLastLog("topic_tracing__.*");
        Path src = getNewFile("logs", lastTracingPath.split("\\.")[0], "txt").toPath();
        Path dst = getNewFile(outPath, "Topic_Tracing", "txt").toPath();
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * A commodity function to write output files with the given content and the given name.
     * @param sb       The content to be written into the log file.
     * @param fileName The name to be assigned to the log file, together with the current timestamp.
     * @throws IOException if it can't write to the created log file.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     */
    private static void writeOutput(StringBuilder sb, String fileName) throws IOException, URISyntaxException {
        File f = getNewFile(outPath, fileName, "txt");
        try (FileWriter writer = new FileWriter(f, false)) {
            writer.append(sb.toString());
        }
    }
}
