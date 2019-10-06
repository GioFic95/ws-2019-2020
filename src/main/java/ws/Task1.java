package ws;

import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.*;
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
        StringBuilder sbCCoeff = new StringBuilder();
        StringBuilder sbCc = new StringBuilder();
        StringBuilder sbBc = new StringBuilder();
        StringBuilder sbAc = new StringBuilder();
        StringBuilder sbPr = new StringBuilder();

        for (int i=2000; i<=2018; i++) {
            for (int k : new int[]{5, 10, 20, 100}) {
                String year = String.valueOf(i);
                Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
                File dot = Utils.getNewFile("graphs/ds1", year, "dot");

                // clustering coefficient
                List<String> topCCoeff = clusteringCoefficient(graph, k);
                Utils.print(topCCoeff);
                GraphUtils.writeImage(dot, "plots/ccoeff", year + "_" + k, topCCoeff);
                sbCCoeff.append(year + "\t" + k + "\t" + topCCoeff + "\n");

                // betweenness centrality
                List<String> topBc = betweennessCentrality(graph, k);
                Utils.print(topBc);
                GraphUtils.writeImage(dot, "plots/bc", year + "_" + k, topBc);
                sbBc.append(year + "\t" + k + "\t" + topBc + "\n");

                // closeness centrality
                List<String> topCc = closenessCentrality(graph, k);
                Utils.print(topCc);
                GraphUtils.writeImage(dot, "plots/cc", year + "_" + k, topCc);
                sbCc.append(year + "\t" + k + "\t" + topCc + "\n");

                // alpha centrality
                List<String> topAc = alphaCentrality(graph, k);
                Utils.print(topAc);
                GraphUtils.writeImage(dot, "plots/ac", year + "_" + k, topAc);
                sbAc.append(year + "\t" + k + "\t" + topAc + "\n");

                // alpha centrality
                List<String> topPr = pageRank(graph, k);
                Utils.print(topPr);
                GraphUtils.writeImage(dot, "plots/pr", year + "_" + k, topPr);
                sbPr.append(year + "\t" + k + "\t" + topPr + "\n");
            }
        }

        Utils.writeLog(sbCCoeff,"clust_coeff");
        Utils.writeLog(sbBc,"between_centr");
        Utils.writeLog(sbCc,"close_centr");
        Utils.writeLog(sbAc,"alpha_centr");
    }

    private static List<String> clusteringCoefficient(Graph<MyVertex, MyEdgeDS1> graph, int k) {
        k = Integer.min(k, graph.vertexSet().size());
        Map<MyVertex, Double> ccScores = new HashMap<>(new ClusteringCoefficient<>(graph).getScores());
        List<String> topCc = new ArrayList<>();
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(ccScores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            ccScores.remove(topVertex);
            topCc.add(topVertex.getId());
        }
        return topCc;
    }

    private static List<String> betweennessCentrality(Graph<MyVertex, MyEdgeDS1> graph, int k) {
        k = Integer.min(k, graph.vertexSet().size());
        Map<MyVertex, Double> bcScores = new HashMap<>(new BetweennessCentrality<>(graph).getScores());
        List<String> topBc = new ArrayList<>();
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(bcScores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            bcScores.remove(topVertex);
            topBc.add(topVertex.getId());
        }
        return topBc;
    }

    private static List<String> closenessCentrality(Graph<MyVertex, MyEdgeDS1> graph, int k) {
        k = Integer.min(k, graph.vertexSet().size());
        Map<MyVertex, Double> bcScores = new HashMap<>(new ClosenessCentrality<>(graph).getScores());
        List<String> topCc = new ArrayList<>();
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(bcScores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            bcScores.remove(topVertex);
            topCc.add(topVertex.getId());
        }
        return topCc;
    }

    private static List<String> alphaCentrality(Graph<MyVertex, MyEdgeDS1> graph, int k) {
        k = Integer.min(k, graph.vertexSet().size());
        Map<MyVertex, Double> acScores = new HashMap<>(new AlphaCentrality<>(graph).getScores());
        List<String> topAc = new ArrayList<>();
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(acScores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            acScores.remove(topVertex);
            topAc.add(topVertex.getId());
        }
        return topAc;
    }

    private static List<String> pageRank(Graph<MyVertex, MyEdgeDS1> graph, int k) {
        k = Integer.min(k, graph.vertexSet().size());
        Map<MyVertex, Double> prScores = new HashMap<>(new PageRank<>(graph).getScores());
        List<String> topPr = new ArrayList<>();
        for (int i=0; i<k; i++) {
            MyVertex topVertex = Collections.max(prScores.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            prScores.remove(topVertex);
            topPr.add(topVertex.getId());
        }
        return topPr;
    }

    public static Map<MyVertex, Double> authorsPageRank(String year) throws ImportException, IOException, URISyntaxException {
        Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
        PageRank<MyVertex, MyEdgeDS1> pageRank = new PageRank<>(graph);
        Map<MyVertex, Double> scores = pageRank.getScores();
        return scores;
    }
}
