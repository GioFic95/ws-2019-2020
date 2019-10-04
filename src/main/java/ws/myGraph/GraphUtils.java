package ws.myGraph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.*;
import org.w3c.dom.Document;
import ws.Utils;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class GraphUtils {

    public static Graph<MyVertex, MyEdgeDS1> loadDS1Graph(String name)
            throws IOException, ImportException, URISyntaxException {
        Graph<MyVertex, MyEdgeDS1> graph = new SimpleGraph<>(MyEdgeDS1.class);
        Path path = Utils.getNewFile("graphs/ds1", name, "dot").toPath();
        Reader reader = Files.newBufferedReader(path);

        VertexProvider<MyVertex> vertexProvider = (id, attributes) -> new MyVertex(id, attributes.get("value").getValue());
        EdgeProvider<MyVertex, MyEdgeDS1> edgeProvider = (from, to, label, attributes) -> new MyEdgeDS1(MyEdgeDS1.getAuthorsFromAttributes(attributes));

        GraphImporter<MyVertex, MyEdgeDS1> importer = new DOTImporter<>(vertexProvider, edgeProvider);
        importer.importGraph(graph, reader);

        return graph;
    }

    public static File saveDS1Graph(Graph<MyVertex, MyEdgeDS1> graph, String name)
            throws ExportException, IOException, URISyntaxException {
        ComponentNameProvider<MyVertex> vertexIDProvider = MyVertex::getId;
        ComponentNameProvider<MyVertex> vertexLabelProvider = MyVertex::getId;
        ComponentAttributeProvider<MyVertex> vertexAttributeProvider = MyVertex::getAttribute;
        ComponentAttributeProvider<MyEdgeDS1> edgeAttributeProvider = MyEdgeDS1::getAttributes;

        GraphExporter<MyVertex, MyEdgeDS1> exporter = new DOTExporter<>(vertexIDProvider, vertexLabelProvider,
                null, vertexAttributeProvider, edgeAttributeProvider);
        File file = Utils.getNewFile("graphs/ds1", name, "dot");
        Writer writer = new FileWriter(file);
        exporter.exportGraph(graph, writer);
        return file;
    }

    public static Graph<MyVertex, MyEdgeDS2> loadDS2Graph(String name)
            throws IOException, ImportException, URISyntaxException {
        Graph<MyVertex, MyEdgeDS2> graph = new SimpleGraph<>(MyEdgeDS2.class);
        Path path = Utils.getNewFile("graphs/ds2", name, "dot").toPath();
        Reader reader = Files.newBufferedReader(path);

        VertexProvider<MyVertex> vertexProvider = (id, attributes) -> new MyVertex(id, attributes.get("value").getValue());
        EdgeProvider<MyVertex, MyEdgeDS2> edgeProvider = (from, to, label, attributes) -> new MyEdgeDS2(MyEdgeDS2.getCollaborationsFromAttributes(attributes));

        GraphImporter<MyVertex, MyEdgeDS2> importer = new DOTImporter<>(vertexProvider, edgeProvider);
        importer.importGraph(graph, reader);

        return graph;
    }

    public static File saveDS2Graph(Graph<MyVertex, MyEdgeDS2> graph, String name)
            throws ExportException, IOException, URISyntaxException {
        ComponentNameProvider<MyVertex> vertexIDProvider = MyVertex::getId;
        ComponentNameProvider<MyVertex> vertexLabelProvider = MyVertex::getId;
        ComponentNameProvider<MyEdgeDS2> edgeLabelProvider = component -> String.valueOf(component.getCollaborations());
        ComponentAttributeProvider<MyVertex> vertexAttributeProvider = MyVertex::getAttribute;
        ComponentAttributeProvider<MyEdgeDS2> edgeAttributeProvider = MyEdgeDS2::getAttributes;

        GraphExporter<MyVertex, MyEdgeDS2> exporter = new DOTExporter<>(vertexIDProvider, vertexLabelProvider,
                edgeLabelProvider, vertexAttributeProvider, edgeAttributeProvider);
        File file = Utils.getNewFile("graphs/ds2", name, "dot");
        Writer writer = new FileWriter(file);
        exporter.exportGraph(graph, writer);
        return file;
    }

    public static void writeImage(File dot, String path, String name) throws IOException, URISyntaxException {
        MutableGraph g = Parser.read(dot);
        File imgFile = Utils.getNewFile(path, name, "svg");
        Utils.print(imgFile.getParentFile().exists());
        Graphviz.fromGraph(g).width(700).render(Format.SVG).toFile(imgFile);
    }

    public static void writeImage(Graph g, String path, String name) throws URISyntaxException, TransformerException {
        JGraphXAdapter<Object, DefaultEdge> graphAdapter = new JGraphXAdapter<Object, DefaultEdge>(g);
        graphAdapter.selectEdges();
        System.out.println(Arrays.toString(graphAdapter.setCellStyles(mxConstants.STYLE_NOLABEL, "1")));
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        Document svgDoc = mxCellRenderer.createSvgDocument(graphAdapter, null, 2, null, null);
        File svgFile = Utils.getNewFile(path, name, "svg");
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(svgFile);
        Source input = new DOMSource(svgDoc);
        transformer.transform(input, output);
    }

}
