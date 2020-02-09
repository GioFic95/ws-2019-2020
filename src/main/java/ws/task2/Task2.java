package ws.task2;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import ws.Utils;
import ws.myGraph.MyVertex;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class Task2 {
    public static void traceTopics(String fileNamePattern, double threshold) throws URISyntaxException {
        String fileName;
        if (fileNamePattern == null || fileNamePattern.equals("")) {
            fileName = Utils.findLastLog("ic_results_merged2__.*\\.txt");
        } else {
            fileName = Utils.findLastLog(fileNamePattern);
        }
        Utils.print("file name: " + fileName);

        IterableResult<Record, ParsingContext> ir = Utils.readTSV(new String[]{"year", "numSeeds", "infectedNodes"}, "logs/" + fileName);
        Type type = new TypeToken<Map<Set<MyVertex>, Set<MyVertex>>>(){}.getType();
        Map<String, Map<String, Set<Set<String>>>> data = new TreeMap<>();

        // read merge output log
        for (Record row : ir) {
            String year = row.getString("year");
            String numSeeds = row.getString("numSeeds");
            String infectedNodesJson = row.getString("infectedNodes");
            Map<Set<MyVertex>, Set<MyVertex>> infectedNodes;
            Set<Set<String>> topics = new HashSet<>();
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

            // populate data
            data.merge(numSeeds, Map.of(year, topics), (oldMap, newMap) -> {
                Map<String, Set<Set<String>>> resMap = Map.copyOf(oldMap);
                resMap.merge(year, topics, (oldSet, newSet) -> {
                    Set<Set<String>> resSet = new HashSet<>(oldSet);
                    resSet.addAll(newSet);
                    return resSet;
                });
                return resMap;
            });
        }

        // todo (i print devono diventare write)
        // trace topics among years
        data.forEach((numSeed, years) -> {
            Utils.print(numSeed + "\n");
            years.forEach((year1, topics1) -> {
                Iterator<Set<String>> topics1it = topics1.iterator();
                topics1it.forEachRemaining(topic1 -> {
                    Utils.print("\n" + topic1 + " (" + year1 + ")");
                    topics1it.remove();
                    years.forEach((year2, topics2) -> {
                        if (year2.compareTo(year1) > 0) {
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
                                Utils.print(" --> " + nextTopic + " (" + year2 + ")");
                                topics2.remove(nextTopic);
                            }
                        }
                    });
                });
            });
            Utils.print("\n\n\n");
        });
    }
}
