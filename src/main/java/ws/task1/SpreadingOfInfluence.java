package ws.task1;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.io.ImportException;
import ws.Utils;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class SpreadingOfInfluence {

    /**
     * To each edge of the the graph from DS1, assign a weight w, s.t. w = sum_{a in A}(pr_a * n_a),
     * where A is the list of authors that use the pair of keywords represented by this edge,
     * pr_a is the PageRank score given to the author a in the graph of DS2 corresponding to the same year of this graph,
     * n_a is the number of times a used this pair of keywords.
     * @param graph todo
     * @param year  todo
     * @return todo
     * @throws ImportException todo
     * @throws IOException todo
     * @throws URISyntaxException todo
     */
    private static Map<MyEdgeDS1, Double> getPropagationProbabilities(Graph<MyVertex, MyEdgeDS1> graph, String year)
            throws ImportException, IOException, URISyntaxException {
        Map<MyVertex, Double> authorsPR = GraphUtils.authorsPageRank(year);
        Map<MyEdgeDS1, Double> probabilities = new HashMap<>();

        // To each edge of the the graph from DS1, assign a weight w, s.t. w = sum_{a in A}(pr_a),
        // where A is the list of authors that use the pair of keywords represented by that edge,
        // pr_a is the PageRank score given to the author a in the graph of DS2 corresponding to the same
        // year of this graph.
        for (MyEdgeDS1 myEdge : graph.edgeSet()) {
            Map<String, Integer> authors = myEdge.getAuthors();
            double prob = authorsPR.entrySet().stream()
                    .filter(entry -> authors.containsKey(entry.getKey().getValue()))
                    .mapToDouble(entry -> entry.getValue() * authors.get(entry.getKey().getValue())).sum();
            probabilities.put(myEdge, prob);
        }
        // Normalize with the max weight.
        double max = Collections.max(probabilities.values());
        probabilities = probabilities.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> Math.log(entry.getValue())/Math.log(max)));
        Utils.print("PropagationProbabilities: " + probabilities);
        return probabilities;
    }

    /**
     * Independent Cascade
     * todo
     * @param graph todo
     * @param seeds todo
     * @return todo
     * @see <a href="http://www.sumankundu.info/articles/detail/How-To-Code-Independent-Cascade-Model-of-Information-Diffusion#lis:Single-Diffusion" target="_blank">Independent Cascade Model of Information Diffusion - Suman Kundu</a>.
     */
    static Map<String, List<String>> independentCascade(Graph<MyVertex, MyEdgeDS1> graph, String year, List<String> seeds)
            throws ImportException, IOException, URISyntaxException {
        NeighborCache<MyVertex, MyEdgeDS1> neighborGraph = new NeighborCache<>(graph);
        Map<MyEdgeDS1, Double> probabilities = getPropagationProbabilities(graph, year);
        Random ran = new Random();

        Set<MyVertex> active = new HashSet<>(); //will store the active nodes
        Stack<MyVertex> target = new Stack<>(); //will store unprocessed nodes during intermediate time
        Map<String, List<String>> result = new HashMap<>(); //will store the results

        for (String seed : seeds) {
            MyVertex s = graph.vertexSet().stream().filter(myVertex -> myVertex.getId().equals(seed)).findAny().get();
            List<String> newNodes = new ArrayList<>();
            target.push(s);
            while (target.size() > 0) {
                MyVertex node = target.pop();
                active.add(node);
                newNodes.add(node.getId());

                for (MyVertex follower : neighborGraph.neighborsOf(node)) {
                    MyEdgeDS1 myEdge = graph.getEdge(s, node);
                    float randnum = ran.nextFloat();
                    double prob = probabilities.get(myEdge);
                    Utils.print("rand: " + randnum + ", prob: " + prob);
                    if (randnum <= prob) {
                        if (!active.contains(follower)) {
                            target.push(follower);
                        }
                    }
                }
            }
            result.put(s.getId(), newNodes);
        }
        return result;
    }
}
