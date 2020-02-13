package ws.task2;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import ws.utils.Utils;
import ws.myGraph.MyVertex;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static ws.utils.Utils.findLastLog;
import static ws.utils.Utils.writeLog;

/**
 * Class used to perform all the jobs related to the second task.
 */
public class Task2 {

    /**
     * Trace the topics behavior among years, for each k in {5, 10, 20, 100}.
     * @param fileNamePattern The pattern to be used to find the log file with the merge information.
     * @param threshold       Two topics of different years are merged if their similarity is grater than the threshold.
     *                        For this purpose the overlap coefficient is used.
     * @throws URISyntaxException if raised by {@link Utils#findLastLogs} or {@link Utils#writeLog}.
     * @throws IOException if raised by {@link Utils#writeLog}.
     * @see <a href="https://en.wikipedia.org/wiki/Overlap_coefficient" target="_blank">Overlap Coefficient</a>.
     */
    public static void traceTopics(String fileNamePattern, double threshold) throws URISyntaxException, IOException {
        String fileName;
        if (fileNamePattern == null || fileNamePattern.equals("")) {
            fileName = findLastLog("ic_results_merged2__.*\\.txt");
        } else {
            fileName = findLastLog(fileNamePattern);
        }
        Utils.print("file name: " + fileName);

        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "numSeeds", "infectedNodes"}, "logs/" + fileName);
        Type type = new TypeToken<Map<Set<MyVertex>, Set<MyVertex>>>(){}.getType();
        Map<Integer, Map<Integer, Set<Set<String>>>> data = new TreeMap<>();
        StringBuilder sb = new StringBuilder();
        int lastNumSeeds = 0;

        // read merge output log
        for (Record row : ir) {
            String yearJson = row.getString("year");
            int year;
            String numSeedJson = row.getString("numSeeds");
            int numSeeds;
            String infectedNodesJson = row.getString("infectedNodes");
            Map<Set<MyVertex>, Set<MyVertex>> infectedNodes;
            Set<Set<String>> topics = new HashSet<>();
            try {
                year = Integer.parseInt(yearJson);
            } catch (NumberFormatException ex) {
                Utils.print("Can't parse string '" + yearJson + "' as Integer.");
                continue;
            }
            try {
                numSeeds = Integer.parseInt(numSeedJson);
            } catch (NumberFormatException ex) {
                Utils.print("Can't parse string '" + numSeedJson + "' as Integer.");
                continue;
            }
            try {
                infectedNodes = MyVertex.getGson().fromJson(infectedNodesJson, type);
            } catch (JsonSyntaxException ex) {
                Utils.print("Can't parse string '" + infectedNodesJson + "' as json.");
                continue;
            }
            infectedNodes.forEach((k,v) -> {
                Set<String> topic = k.stream().map(MyVertex::getValue).collect(Collectors.toSet());
                topic.addAll(v.stream().map(MyVertex::getValue).collect(Collectors.toSet()));
                topics.add(topic);
            });

            if (20 < numSeeds && numSeeds < 100) {
                numSeeds = 100;
            } else if (10 < numSeeds && numSeeds < 20) {
                if (lastNumSeeds == 20) {
                    numSeeds = 100;
                } else {
                    numSeeds = 20;
                }
            }

            // populate data
            data.merge(numSeeds, Map.of(year, topics), (oldMap, newMap) -> {
                Map<Integer, Set<Set<String>>> resMap = new TreeMap<>(oldMap);
                resMap.merge(year, topics, (oldSet, newSet) -> {
                    Set<Set<String>> resSet = new HashSet<>(oldSet);
                    resSet.addAll(newSet);
                    return resSet;
                });
                return resMap;
            });

            lastNumSeeds = numSeeds;
        }

        // trace topics among years
        data.forEach((numSeed, years) -> {
            sb.append(numSeed + "\n");
            years.forEach((year1, topics1) -> {
                Iterator<Set<String>> topics1it = topics1.iterator();
                topics1it.forEachRemaining(topic1 -> {
                    sb.append("\n" + topic1 + " (" + year1 + ")");
                    topics1it.remove();
                    years.forEach((year2, topics2) -> {
                        if (year2 > year1) {
                            Map<Set<String>, Double> similarities = new HashMap<>();
                            topics2.forEach(topic2 -> {
                                Set<String> intersect = new HashSet<>(topic1);
                                intersect.retainAll(topic2);
                                double sim = (double) intersect.size() / Math.min(topic1.size(), topic2.size());
                                if (sim > threshold) {
                                    similarities.put(topic2, sim);
                                }
                            });
                            Optional<Map.Entry<Set<String>, Double>> nextTopicOpt = similarities.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue));
                            if (nextTopicOpt.isPresent()) {
                                Set<String> nextTopic = nextTopicOpt.get().getKey();
                                sb.append(" --> " + nextTopic + " (" + year2 + ")");
                                topics2.remove(nextTopic);
                            }
                        }
                    });
                });
            });
            sb.append("\n\n\n");
        });
        writeLog(sb, "topic_tracing");
    }
}
