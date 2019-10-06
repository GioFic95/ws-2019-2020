package ws;

import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClusteringCoefficient;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import org.jgrapht.io.ImportException;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Task1 {

    public static void tryMeasures() throws URISyntaxException, IOException, ImportException {
        StringBuilder sbCc = new StringBuilder();
        StringBuilder sbBc = new StringBuilder();

        for (int i=2000; i<=2018; i++) {
            String year = String.valueOf(i);
            Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
            File dot = Utils.getNewFile("graphs/ds1", year, "dot");

            // clustering coefficient
            List<String> topCc = clusteringCoefficient(graph, 10);
            Utils.print(topCc);
            GraphUtils.writeImage(dot, "plots/cc", year, topCc);
            sbCc.append(year + "\t" + topCc + "\n");

            // betweenness centrality
            List<String> topBc = betweennessCentrality(graph, 10);
            Utils.print(topBc);
            GraphUtils.writeImage(dot, "plots/bc", year, topBc);
            sbBc.append(year + "\t" + topBc + "\n");
        }

        Utils.writeLog(sbCc,"cc");
        Utils.writeLog(sbBc,"bc");
    }

    private static List<String> clusteringCoefficient(Graph<MyVertex, MyEdgeDS1> graph, int k) {
        ClusteringCoefficient<MyVertex, MyEdgeDS1> cc = new ClusteringCoefficient<>(graph);
        Map<MyVertex, Double> ccScores = new HashMap<>(cc.getScores());
        List<String> topCc = new ArrayList<>();
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(ccScores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            ccScores.remove(topVertex);
            topCc.add(topVertex.getId());
        }
        return topCc;
    }

    private static List<String> betweennessCentrality(Graph<MyVertex, MyEdgeDS1> graph, int k) {
        BetweennessCentrality<MyVertex, MyEdgeDS1> bc = new BetweennessCentrality<>(graph);
        Map<MyVertex, Double> bcScores = new HashMap<>(bc.getScores());
        List<String> topBc = new ArrayList<>();
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(bcScores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            bcScores.remove(topVertex);
            topBc.add(topVertex.getId());
        }
        return topBc;
    }

    private static void authorsPageRank(Graph<MyVertex, DefaultWeightedEdge> graph) {

    }
}
