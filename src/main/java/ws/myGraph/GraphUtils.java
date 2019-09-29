package ws.myGraph;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.*;
import ws.Main;
import ws.Utils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GraphUtils {

    // TODO remove this method (?)
    public static void demo() throws ExportException, IOException, ImportException {
        // create default graph
        Graph<MyVertex, MyEdge> myGraph = createMyGraphDefault();
        Utils.print("-- toString output");
        String myGraphTxt = myGraph.toString();
        Utils.print(myGraphTxt);
        Utils.print("");

        // store dot and plot of the graph
        File myFile = saveMyGraph(myGraph, "MyGraph0");
        writeImage(myFile, "MyGraph0");
        Utils.print("");

        // load the graph and check if the loaded graph is equal to the original one
        Graph<MyVertex, MyEdge> myGraph1 = loadMyGraph("MyGraph0");
        String myGraphTxt1 = myGraph1.toString();
        Utils.print(myGraphTxt1);
        Utils.print("two graphs are equal: " + myGraphTxt.equals(myGraphTxt1));
        File myFile1 = saveMyGraph(myGraph1, "MyGraph1");
        GraphUtils.writeImage(myFile1, "MyGraph1");
    }

    // TODO remove this method (?)
    public static Graph<MyVertex, MyEdge> createMyGraphDefault() {
        Graph<MyVertex, MyEdge> myGraph = new SimpleGraph<>(MyEdge.class);

        MyVertex v1 = new MyVertex("v1");
        Utils.print(v1);
        MyVertex v2 = new MyVertex("v2");
        MyVertex v3 = new MyVertex("v3");
        MyVertex v4 = new MyVertex("v4");

        // add the vertices
        myGraph.addVertex(v1);
        myGraph.addVertex(v2);
        myGraph.addVertex(v3);
        myGraph.addVertex(v4);

        // add edges to create a circuit
        Map<String, Integer> authors1 = new HashMap<>();
        authors1.put("Mario", 1);
        authors1.put("Gino", 2);
        authors1.put("Ugo", 3);
        myGraph.addEdge(v1, v2, new MyEdge(authors1));

        Map<String, Integer> authors2 = new HashMap<>();
        authors2.put("Mario", 4);
        authors2.put("Gino", 3);
        authors2.put("Ugo", 1);
        myGraph.addEdge(v2, v3, new MyEdge(authors2));

        Map<String, Integer> authors3 = new HashMap<>();
        authors3.put("Mario", 2);
        authors3.put("Gino", 1);
        authors3.put("Ugo", 6);
        myGraph.addEdge(v3, v4, new MyEdge(authors3));

        Map<String, Integer> authors4 = new HashMap<>();
        authors4.put("Mario", 7);
        authors4.put("Gino", 7);
        authors4.put("Ugo", 7);
        myGraph.addEdge(v4, v1, new MyEdge(authors4));

        return myGraph;
    }

    public static Graph<MyVertex, MyEdge> loadMyGraph(String name) throws IOException, ImportException {
        Graph<MyVertex, MyEdge> myGraph = new SimpleGraph<>(MyEdge.class);
        Path path = getNewFile("graphs", name, "dot").toPath();   // TODO change txt --> dot
        Reader reader = Files.newBufferedReader(path);

        VertexProvider<MyVertex> vertexProvider = (id, attributes) -> new MyVertex(id, attributes.get("value").getValue());
        EdgeProvider<MyVertex, MyEdge> edgeProvider = (from, to, label, attributes) -> new MyEdge(MyEdge.getAuthorsFromAttributes(attributes));

        GraphImporter<MyVertex, MyEdge> importer = new DOTImporter<>(vertexProvider, edgeProvider);
        importer.importGraph(myGraph, reader);

        return myGraph;
    }

    public static File saveMyGraph(Graph<MyVertex, MyEdge> myGraph, String name) throws ExportException, IOException {
        ComponentNameProvider<MyVertex> vertexIDProvider = MyVertex::getId;
        ComponentNameProvider<MyVertex> vertexLabelProvider = MyVertex::getId;
        ComponentNameProvider<MyEdge> edgeLabelProvider = DefaultEdge::toString;
        ComponentAttributeProvider<MyVertex> vertexAttributeProvider = MyVertex::getAttribute;
        ComponentAttributeProvider<MyEdge> edgeAttributeProvider = MyEdge::getAttributes;

        GraphExporter<MyVertex, MyEdge> exporter = new DOTExporter<>(vertexIDProvider, vertexLabelProvider,
                null, vertexAttributeProvider, edgeAttributeProvider);
        File file = getNewFile("graphs", name, "dot");
        Writer writer = new FileWriter(file);
        exporter.exportGraph(myGraph, writer);
        return file;
    }

    public static void writeImage(File dot, String name) {
        try {
            MutableGraph g = Parser.read(dot);
            File imgFile = getNewFile("plots", name, "png");
            Utils.print(imgFile.getParentFile().exists());
            Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(imgFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static File getNewFile(String pathName, String fileName, String ext) {
        URI res = null;
        try {
            res = Main.class.getResource(pathName).toURI();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        System.out.println(fileName + " " + new File(res).exists());

        String path = res.getPath() + "/" + fileName + "." + ext;
        System.out.println(path);
        return new File(path);
    }

}
