package ws;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.ImportException;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;
import ws.myGraph.SimpleDirectedEdge;
import ws.task1.DiffusionUtils;
import ws.weights.PageRankWeight;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.*;

import static ws.myGraph.GraphUtils.*;

public class Tests {
    private Tests() {} // ensure non-instantiability.

    public static void main(String[] args) throws URISyntaxException, ExportException, IOException, ImportException {
        // test dataset 1 stuff
//        demoDS1();

        // test dataset 2 stuff
//        demoDS2();

        // test PageRankWeight
//        Graph<MyVertex, MyEdgeDS1> g = GraphUtils.loadDS1Graph("2018");
//        PageRankWeight prw = new PageRankWeight(g, "2018", "test");
//        Utils.print(prw.getScores());

        // test delete files according to pattern
//        Utils.delMatchigFiles("test", "(scoring|simple_weight|page_rank)[a-zA-Z_]*\\.txt");

        // test for computing probabilities on directed edges in undirected graphs
//        testDirectedEdgesProbabilities();

        // test the functioning of the serialization and deserialization of maps containing MyVertex objects
        testMyVertexSerialization();
    }

    public static void demoDS1() throws ExportException, IOException, ImportException, URISyntaxException {
        // create default graph
        Graph<MyVertex, MyEdgeDS1> graph = createMyGraphDS1Default();
        Utils.print("-- toString output");
        String graphTxt = graph.toString();
        Utils.print(graphTxt);
        Utils.print("");

        // store dot and plot of the graph
        File myFile = saveDS1Graph(graph, "MyGraphDS1_0");
        writeImage(myFile, "plots/ds1", "MyGraphDS1_0");
        Utils.print("");

        // load the graph and check if the loaded graph is equal to the original one
        Graph<MyVertex, MyEdgeDS1> graph1 = loadDS1Graph("MyGraphDS1_0");
        String graphTxt1 = graph1.toString();
        Utils.print(graphTxt1);
        File myFile1 = saveDS1Graph(graph1, "MyGraphDS1_1");
        writeImage(myFile1, "plots/ds1", "MyGraphDS1_1");

        Utils.print("two graphs are equal: " + graph.equals(graph1));
    }

    @org.jetbrains.annotations.NotNull
    public static Graph<MyVertex, MyEdgeDS1> createMyGraphDS1Default() {
        Graph<MyVertex, MyEdgeDS1> graph = new SimpleGraph<>(MyEdgeDS1.class);

        MyVertex v1 = new MyVertex("v1");
        Utils.print(v1);
        MyVertex v2 = new MyVertex("v2");
        MyVertex v3 = new MyVertex("v3");
        MyVertex v4 = new MyVertex("v4");

        // add the vertices
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);

        // add edges to create a circuit
        Map<String, Integer> authors1 = new HashMap<>();
        authors1.put("Mario", 1);
        authors1.put("Gino", 2);
        authors1.put("Ugo", 3);
        graph.addEdge(v1, v2, new MyEdgeDS1(authors1));

        Map<String, Integer> authors2 = new HashMap<>();
        authors2.put("Mario", 4);
        authors2.put("Gino", 3);
        authors2.put("Ugo", 1);
        graph.addEdge(v2, v3, new MyEdgeDS1(authors2));

        Map<String, Integer> authors3 = new HashMap<>();
        authors3.put("Mario", 2);
        authors3.put("Gino", 1);
        authors3.put("Ugo", 6);
        graph.addEdge(v3, v4, new MyEdgeDS1(authors3));

        Map<String, Integer> authors4 = new HashMap<>();
        authors4.put("Mario", 7);
        authors4.put("Gino", 7);
        authors4.put("Ugo", 7);
        graph.addEdge(v4, v1, new MyEdgeDS1(authors4));

        return graph;
    }

    public static void demoDS2() throws ExportException, IOException, ImportException, URISyntaxException {
        // create default graph
        Graph<MyVertex, DefaultWeightedEdge> graph = createMyGraphDS2Default();
        Utils.print("-- toString output");
        String graphTxt = graph.toString();
        Utils.print(graphTxt);
        Utils.print("");

        // store dot and plot of the graph
        File myFile = saveDS2Graph(graph, "MyGraphDS2_0");
        writeImage(myFile, "plots/ds2", "MyGraphDS2_0");
        Utils.print("");

        // load the graph and check if the loaded graph is equal to the original one
        Graph<MyVertex, DefaultWeightedEdge> graph1 = loadDS2Graph("MyGraphDS2_0");
        String graphTxt1 = graph1.toString();
        Utils.print(graphTxt1);
        Utils.print("two graphs are equal: " + graphTxt.equals(graphTxt1));
        File myFile1 = saveDS2Graph(graph1, "MyGraphDS2_1");
        writeImage(myFile1, "plots/ds2", "MyGraphDS2_1");

        // change color of some node
        String[] s = new String[]{"v2", "v3"};
        writeImage(myFile1, "plots/ds2", "MyGraphDS2_2", Arrays.asList(s));
    }

    @org.jetbrains.annotations.NotNull
    public static Graph<MyVertex, DefaultWeightedEdge> createMyGraphDS2Default() {
        Graph<MyVertex, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        MyVertex v1 = new MyVertex("v1");
        MyVertex v2 = new MyVertex("v2");
        MyVertex v3 = new MyVertex("v3");
        MyVertex v4 = new MyVertex("v4");

        // add the vertices
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);

        // add edges to create a circuit
        DefaultWeightedEdge e1 = graph.addEdge(v1, v2);
        graph.setEdgeWeight(e1, 2);
        DefaultWeightedEdge e2 = graph.addEdge(v2, v3);
        graph.setEdgeWeight(e2, 1);
        DefaultWeightedEdge e3 = graph.addEdge(v3, v4);
        graph.setEdgeWeight(e3, 2);
        graph.addVertex(new MyVertex("v1"));
        DefaultWeightedEdge e4 = graph.addEdge(v4, v1);
        graph.setEdgeWeight(e4, 3);

        return graph;
    }

    private static void testDirectedEdgesProbabilities() throws ImportException, IOException, URISyntaxException {
        Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph("2000");
        Map<SimpleDirectedEdge, Double> probabilities = DiffusionUtils.getEdgePropagationProbabilities(graph, "2000");
        MyVertex v1 = new MyVertex("43.80.+p");
        MyVertex v2 = new MyVertex("lyapunov functional");
        SimpleDirectedEdge edge1 = new SimpleDirectedEdge(v1, v2);
        SimpleDirectedEdge edge2 = new SimpleDirectedEdge(v2, v1);
        Utils.print(probabilities.get(edge1));
        Utils.print(probabilities.get(edge2));
    }

    private static void testMyVertexSerialization() {
        Map<String, Set<String>> map4 = new HashMap<>();
        Set<String> s1 = new HashSet<>();
        s1.add("1a");
        s1.add("1b");
        Set<String> s2 = new HashSet<>();
        s2.add("2a");
        s2.add("2b");
        s2.add("2c");
        map4.put("uno", s1);
        map4.put("due", s2);
        Utils.print(map4);
        String json = new Gson().toJson(map4);
        Utils.print(json);
        Type type = new TypeToken<Map<String, Set<String>>>(){}.getType();
        Map<Integer, String> obj = new Gson().fromJson(json, type);
        Utils.print(obj);

        Type type2 = new TypeToken<Map<MyVertex, String>>(){}.getType();
        Type type3 = new TypeToken<Map<MyVertex, Set<MyVertex>>>(){}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MyVertex.class, new MyVertex.MyVertexDeserializer())
                .registerTypeAdapter(MyVertex.class, new MyVertex.MyVertexSerializer())
                .registerTypeAdapter(type2, new MyMapOfMyVertexToStringSerializer())
                .registerTypeAdapter(type3, new MyVertex.MyMapOfMyVertexToMyVertexesSerializer())
                .create();

        Utils.print("test 1");
        String json1 = gson.toJson(new MyVertex("cinque"));
        Utils.print(json1);
        MyVertex obj1 = gson.fromJson(json1, MyVertex.class);
        Utils.print(obj1);

        Utils.print("test 2");
        Map<MyVertex, String> map6 = new HashMap<>();
        map6.put(new MyVertex("uno"), "1");
        map6.put(new MyVertex("due"), "2");
        Utils.print(map6);
        String json2 = gson.toJson(map6, type2);
        Utils.print(json2);
        Map<Integer, String> obj2 = gson.fromJson(json2, type2);
        Utils.print(obj2);

        Utils.print("test 3");
        Map<MyVertex, Set<MyVertex>> map5 = new HashMap<>();
        Set<MyVertex> vs1 = new HashSet<>();
        vs1.add(new MyVertex("val1a"));
        vs1.add(new MyVertex("val1b"));
        Set<MyVertex> vs2 = new HashSet<>();
        vs2.add(new MyVertex("val2a"));
        vs2.add(new MyVertex("val2b"));
        vs2.add(new MyVertex("val2c"));
        map5.put(new MyVertex("uno"), vs1);
        map5.put(new MyVertex("due"), vs2);
        Utils.print(map5);
        String json3 = gson.toJson(map5, type3);
        Utils.print(json3);
        Map<MyVertex, Set<MyVertex>> obj3 = gson.fromJson(json3, type3);
        Utils.print(obj3);
    }

    private static class MyMapOfMyVertexToStringSerializer implements JsonSerializer<Map<MyVertex, String>> {
        @Override
        public JsonElement serialize(Map<MyVertex, String> myVertexStringMap, Type type, JsonSerializationContext jsonSerializationContext) {
            Utils.print("MyMapOfMyVertexToStringSerializer " + myVertexStringMap);
            JsonObject jo = new JsonObject();
            myVertexStringMap.forEach((k, v) -> {
                JsonElement je = new MyVertex.MyVertexSerializer().serialize(k, type, jsonSerializationContext);
                jo.add(je.getAsString(), new JsonPrimitive(v));
            });
            return jo;
        }
    }
}
