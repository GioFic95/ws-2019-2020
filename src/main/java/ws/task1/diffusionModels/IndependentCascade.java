package ws.task1.diffusionModels;

import com.google.gson.reflect.TypeToken;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.NeighborCache;
import ws.utils.Utils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;
import ws.myGraph.SimpleDirectedEdge;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class IndependentCascade extends DiffusionModel{
    private Map<SimpleDirectedEdge, Double> propagationProbabilities;
    private NeighborCache<MyVertex, MyEdgeDS1> neighborGraph;
    private String year;
    private String name;

    public IndependentCascade(String name, String year, Graph<MyVertex, MyEdgeDS1> graph,
                              List<String> seeds, Map<SimpleDirectedEdge, Double> edgeProbabilities) {
        super(graph, seeds);
        this.neighborGraph = new NeighborCache<>(graph);
        this.propagationProbabilities = edgeProbabilities;
        this.year = year;
        this.name = name;
    }

    /**
     *
     * @return
     * @see <a href="https://github.com/GiulioRossetti/ndlib/blob/master/ndlib/models/epidemics/IndependentCascadesModel.py" target="_blank">Independent Cascade Model - NDLIB</a>.
     */
    @Override
    public Set<String> iteration() {
        Map<MyVertex, NodeStatus> currentStatuses = new HashMap<>(statuses);

        Map<MyVertex, Set<MyVertex>> infectedNodes = currentStatuses.entrySet().stream()
                .filter(entry -> entry.getValue() == NodeStatus.INFECTED)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new HashSet<>()));

        currentStatuses.forEach((k, v) -> {
            if (v == NodeStatus.INFECTED) {
                infectedNodes.put(k, new HashSet<>());
            }
        });

        if (currentIteration == 0) {
            currentIteration ++;
            return new HashSet<>(seeds);
        } else {
            for (Map.Entry<MyVertex, NodeStatus> entry : statuses.entrySet()) {
                if (entry.getValue() != NodeStatus.INFECTED) {
                    continue;
                } else {
                    MyVertex v1 = entry.getKey();
                    Set<MyVertex> neighbors = neighborGraph.neighborsOf(v1);
                    if (! neighbors.isEmpty()) {
                        for (MyVertex v2 : neighbors) {
                            if (currentStatuses.get(v2) == NodeStatus.SUSCEPTIBLE) {
                                Double threshold = propagationProbabilities.get(new SimpleDirectedEdge(v1, v2));
                                Double coin = new Random().nextDouble();
                                StringBuilder sb_coins = new StringBuilder()
                                        .append(v1.getId()).append(" - ").append(v2.getId()).append("\t")
                                        .append("coin\t").append(coin)
                                        .append("\tvs threshold\t").append(threshold)
                                        .append("\n");
                                try {
                                    Utils.writeLog(sb_coins, "coins", false);
                                } catch (IOException | URISyntaxException e) {
                                    System.err.println("couldn't write coins log");
                                    e.printStackTrace();
                                }
                                if (coin <= threshold) {
                                    currentStatuses.put(v2, NodeStatus.INFECTED);
                                    infectedNodes.get(v1).add(v2);
                                }
                            }
                        }
                    }
                    currentStatuses.put(v1, NodeStatus.REMOVED);
                }
            }

            // write result log
            Utils.print("infectedNodes: " + infectedNodes);
            Type type = new TypeToken<Map<MyVertex, Set<MyVertex>>>(){}.getType();
            String jsonInfectedNodes = MyVertex.getGson().toJson(infectedNodes, type);
            StringBuilder sb_iterations = new StringBuilder()
                    .append(year).append("\t").append(currentIteration).append("\t").append(jsonInfectedNodes).append("\n");
            try {
                Utils.writeLog(sb_iterations, name, false);
            } catch (IOException | URISyntaxException e) {
                System.err.println("couldn't write independent cascade iterations log");
                e.printStackTrace();
            }

            statuses = currentStatuses;
            currentIteration ++;
            return statuses.entrySet().stream()
                    .filter(e -> e.getValue() == NodeStatus.INFECTED)
                    .map(e -> e.getKey().getId())
                    .collect(Collectors.toSet());
        }
    }
}
