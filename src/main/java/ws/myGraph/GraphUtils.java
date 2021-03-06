package ws.myGraph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.io.*;
import org.w3c.dom.Document;
import ws.utils.Utils;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class with utility functions for reading, storing ad plotting graphs.<br>
 * It uses <a href="https://jgrapht.org/" target="_blank">JGraphT</a> and
 * <a href="https://github.com/nidi3/graphviz-java" target="_blank">graphviz-java</a>.
 */
public class GraphUtils {

    /**
     * Read a {@link Graph} from a DOT file, relative to DS2.
     * @param name The file name for the graph to be loaded.
     * @return The {@link Graph} contained in the input file.
     * @throws IOException if it can't read the given file.
     * @throws ImportException if it can't import the graph from the given file.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     */
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

    /**
     * Persistently store a graph relative to DS1 into a DOT file.
     * @param graph The input graph from DS1.
     * @param name  The name to be used to store the DOT file with the serialized graph.
     * @return A {@link File} containing the input graph, serialize in DOT format.
     * @throws ExportException if it can't export the graph to the given file.
     * @throws IOException if it can't create a {@link FileWriter} for the output file with the given name.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     */
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

    /**
     * Read a {@link Graph} from a DOT file, relative to DS2.
     * @param name The file name for the graph to be loaded.
     * @return The {@link Graph} contained in the input file.
     * @throws IOException if it can't read the given file.
     * @throws ImportException if it can't import the graph from the given file.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     */
    public static Graph<MyVertex, DefaultWeightedEdge> loadDS2Graph(String name)
            throws IOException, ImportException, URISyntaxException {
        SimpleWeightedGraph<MyVertex, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        Path path = Utils.getNewFile("graphs/ds2", name, "dot").toPath();
        Reader reader = Files.newBufferedReader(path);

        VertexProvider<MyVertex> vertexProvider = (id, attributes) -> new MyVertex(id, attributes.get("value").getValue());
        EdgeProvider<MyVertex, DefaultWeightedEdge> edgeProvider = (from, to, label, attributes) -> {
            DefaultWeightedEdge e = new DefaultWeightedEdge();
            graph.addEdge(from, to, e);
            graph.setEdgeWeight(e, Double.parseDouble(attributes.get("weight").getValue()));
            return e;
        };
        GraphImporter<MyVertex, DefaultWeightedEdge> importer = new DOTImporter<>(vertexProvider, edgeProvider);
        importer.importGraph(graph, reader);

        return graph;
    }

    /**
     * Persistently store a graph relative to DS12into a DOT file.
     * @param graph The input graph from DS2.
     * @param name  The name to be used to store the DOT file with the serialized graph.
     * @return A {@link File} containing the input graph, serialize in DOT format.
     * @throws ExportException if it can't export the graph to the given file.
     * @throws IOException if it can't create a {@link FileWriter} for the output file with the given name.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     */
    public static File saveDS2Graph(Graph<MyVertex, DefaultWeightedEdge> graph, String name)
            throws ExportException, IOException, URISyntaxException {
        ComponentNameProvider<MyVertex> vertexIDProvider = MyVertex::getId;
        ComponentNameProvider<MyVertex> vertexLabelProvider = MyVertex::getId;
        ComponentNameProvider<DefaultWeightedEdge> edgeLabelProvider = component -> String.valueOf(graph.getEdgeWeight(component));
        ComponentAttributeProvider<MyVertex> vertexAttributeProvider = MyVertex::getAttribute;
        ComponentAttributeProvider<DefaultWeightedEdge> edgeAttributeProvider = component -> {
            Map<String, Attribute> attributes = new HashMap<>();
            attributes.put("weight", DefaultAttribute.createAttribute(graph.getEdgeWeight(component)));
            return attributes;
        };
        GraphExporter<MyVertex, DefaultWeightedEdge> exporter = new DOTExporter<>(vertexIDProvider, vertexLabelProvider,
                edgeLabelProvider, vertexAttributeProvider, edgeAttributeProvider);
        File file = Utils.getNewFile("graphs/ds2", name, "dot");
        Writer writer = new FileWriter(file);
        exporter.exportGraph(graph, writer);
        return file;
    }

    /**
     * Creates and stores a plot of the graph written into the given DOT file.
     * It uses {@link Graphviz} to read the graph and produce the plot.
     * @param dot  The DOT file containing the serialized graph.
     * @param path The path where to save the produced image.
     * @param name The name to give to the produced image.
     * @throws IOException if can't read the input file.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     */
    public static void writeImage(File dot, String path, String name) throws IOException, URISyntaxException {
        MutableGraph g = Parser.read(dot);
        File imgFile = Utils.getNewFile(path, name, "svg");
        Graphviz.fromGraph(g).width(700).render(Format.SVG).toFile(imgFile);
    }

    /**
     * Creates and stores a plot of the graph written into the given DOT file, the given nodes are colored.
     * It uses {@link Graphviz} to read the graph and produce the plot.
     * @param dot   The DOT file containing the serialized graph.
     * @param path  The path where to save the produced image.
     * @param name  The name to give to the produced image.
     * @param nodes The keys that identify the nodes to be colored in the output plot.
     * @throws IOException if can't read the input file.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     */
    public static void writeImage(File dot, String path, String name, List<String> nodes) throws IOException, URISyntaxException {
        MutableGraph g = Parser.read(dot);
        g.nodes().forEach(node -> {
            if (nodes.contains(node.name().toString())) {
                node.add(Color.CYAN, Style.FILLED);
            }
        });
        File imgFile = Utils.getNewFile(path, name, "svg");
        Graphviz.fromGraph(g).width(700).render(Format.SVG).toFile(imgFile);
    }

    /**
     * Creates and stores a plot of the graph written into the given DOT file, the given nodes are colored such that
     * nodes in the same topic have the same color, and seeds are rectangular.
     * It uses {@link Graphviz} to read the graph and produce the plot.
     * @param dot   The DOT file containing the serialized graph.
     * @param path  The path where to save the produced image.
     * @param name  The name to give to the produced image.
     * @param nodes A map of seeds and infected nodes, the seeds can be strings or sets of strings (in case more
     *              topics were merged in a single one).
     * @throws IOException if can't read the input file.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     */
    public static void writeImage(File dot, String path, String name, Map<?, Set<String>> nodes) throws IOException, URISyntaxException {
        MutableGraph g = Parser.read(dot);
        Map<String, MutableNode> gNodes = g.nodes().stream().collect(Collectors.toMap(
                entry -> entry.name().toString(), entry -> entry));

        try {
            Map<String, Set<String>> nodes1 = (Map<String, Set<String>>) nodes;
            nodes1.forEach((seed, infected) -> {
                Color c = Utils.getRandColor();

                // make the current seed node colored and rectangular
                MutableNode gSeed = gNodes.get(seed);
                gSeed.add(c, Style.FILLED, Shape.RECTANGLE);

                // make the nodes infected by the current seed colored with the same color
                infected.forEach(infNode -> {
                    MutableNode gInfNode = gNodes.get(infNode);
                    gInfNode.add(c, Style.FILLED);
                });
            });
        } catch (ClassCastException ex1) {
            try {
                Map<Set<String>, Set<String>> nodes2 = (Map<Set<String>, Set<String>>) nodes;
                nodes2.forEach((seeds, infected) -> {
                    Color c = Utils.getRandColor();

                    // make the current seed node colored and rectangular
                    seeds.forEach(seed -> {
                        MutableNode gSeed = gNodes.get(seed);
                        gSeed.add(c, Style.FILLED, Shape.RECTANGLE);
                    });

                    // make the nodes infected by the current seed colored with the same color
                    infected.forEach(infNode -> {
                        MutableNode gInfNode = gNodes.get(infNode);
                        gInfNode.add(c, Style.FILLED);
                    });
                });
            } catch (ClassCastException ex2) {
                throw new IllegalArgumentException("The parameter nodes should be a map of type Map<String, Set<String>> or Map<Set<String>, Set<String>>");
            }
        }

        File imgFile = Utils.getNewFile(path, name, "svg");
        Graphviz.fromGraph(g).width(700).render(Format.SVG).toFile(imgFile);
    }

    /**
     * Creates and stores a plot of the given graph.
     * It uses {@link JGraphXAdapter} and {@link mxCellRenderer} to read the graph and produce the plot.
     * @param g     The input graph.
     * @param path  The path where to save the produced image.
     * @param name  The name to give to the produced image.
     * @throws URISyntaxException if raised by {@link Utils#getNewFile(String, String, String)}).
     * @throws TransformerException if can't actually write the current image.
     */
    public static void writeImage(Graph g, String path, String name) throws URISyntaxException, TransformerException {
        JGraphXAdapter<Object, DefaultEdge> graphAdapter = new JGraphXAdapter<Object, DefaultEdge>(g);
        graphAdapter.selectEdges();
        System.out.println(Arrays.toString(graphAdapter.setCellStyles(mxConstants.STYLE_NOLABEL, "1")));
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        Document svgDoc = mxCellRenderer.createSvgDocument(graphAdapter, null, 0.1, null, null);
        File svgFile = Utils.getNewFile(path, name, "svg");
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(svgFile);
        Source input = new DOMSource(svgDoc);
        transformer.transform(input, output);
    }

    /**
     * Commodity function to compute the page rank of a DS2 graph (i.e., the scores of the authors).
     * @param year The year of the graph whose PageRank is to be computed.
     * @return The PageRank scores of the given graph.
     * @throws ImportException if raised by {@link #loadDS2Graph(String)}
     * @throws IOException if raised by {@link #loadDS2Graph(String)}
     * @throws URISyntaxException if raised by {@link #loadDS2Graph(String)}
     */
    public static Map<MyVertex, Double> authorsPageRank(String year) throws ImportException, IOException, URISyntaxException {
        Graph<MyVertex, DefaultWeightedEdge> graph = loadDS2Graph(year);
        PageRank<MyVertex, DefaultWeightedEdge> pageRank = new PageRank<>(graph);
        return pageRank.getScores();
    }

    /**
     * Produces a map id -> value for the nodes of the graph of the given year in DS1.
     * @param year The year of the graph whose map is to be computed.
     * @return A map id -> value for the nodes of the graph of the given year in DS1.
     * @throws ImportException if raised by {@link #loadDS1Graph(String)}
     * @throws IOException if raised by {@link #loadDS1Graph(String)}
     * @throws URISyntaxException if raised by {@link #loadDS1Graph(String)}
     */
    public static Map<String, String> getGraphMap(String year) throws ImportException, IOException, URISyntaxException {
        Graph<MyVertex, MyEdgeDS1> graph = GraphUtils.loadDS1Graph(year);
        Map<String, String> nodes = new HashMap<>();
        graph.vertexSet().forEach(mv -> nodes.put(mv.getId(), mv.getValue()));
        return nodes;
    }
}
