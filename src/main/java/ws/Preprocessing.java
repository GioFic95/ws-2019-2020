package ws;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import guru.nidi.graphviz.engine.GraphvizException;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.ExportException;
import org.json.JSONObject;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyEdgeDS2;
import ws.myGraph.MyVertex;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static ws.Utils.*;

public class Preprocessing {

    public static IterableResult<Record, ParsingContext> readDS(String[] headers, String path) {
        TsvParserSettings settings = new TsvParserSettings();
        settings.setHeaderExtractionEnabled(false);
        settings.setHeaders(headers);
        TsvParser parser = new TsvParser(settings);

        InputStream ds = Preprocessing.class.getResourceAsStream(path);
        return parser.iterateRecords(ds);
    }

    public static Map<String, Graph<MyVertex, MyEdgeDS1>> createGraphsFromDS1(IterableResult<Record, ParsingContext> iter) {
        Map<String, String> map = new HashMap<>();
        Map<String, Graph<MyVertex, MyEdgeDS1>> graphs = new HashMap<>();

        for (Record row : iter) {
            row.fillFieldMap(map);
            print(map);

            if (map.get("keyword1").contains("??") || map.get("keyword2").contains("??"))
                continue;

            String year = map.get("year");
            Graph<MyVertex, MyEdgeDS1> graph = graphs.computeIfAbsent(year, y -> new SimpleGraph<>(MyEdgeDS1.class));

            MyVertex v1 = new MyVertex(map.get("keyword1"));
            MyVertex v2 = new MyVertex(map.get("keyword2"));
            graph.addVertex(v1);
            graph.addVertex(v2);

            Map<String, Integer> authors = new HashMap<>();
            JSONObject jAuthors = new JSONObject(map.get("authors"));
            for (String k : jAuthors.keySet()) {
                authors.put(k, jAuthors.getInt(k));
            }
            graph.addEdge(v1, v2, new MyEdgeDS1(authors));
        }
        return graphs;
    }

    public static void writeGraphsFromDS1(Map<String, Graph<MyVertex, MyEdgeDS1>> graphs)
            throws ExportException, IOException, URISyntaxException {
        for (Map.Entry<String, Graph<MyVertex, MyEdgeDS1>> entry : graphs.entrySet()) {
            String filename = entry.getKey();
            Graph<MyVertex, MyEdgeDS1> graph = entry.getValue();
            int vertSize = graph.vertexSet().size();
            int edgeSize = graph.edgeSet().size();
            Utils.print("Graph " + filename + ": vertSize " + vertSize + ", edgeSize " + edgeSize);
            File dot = GraphUtils.saveDS1Graph(graph, filename);
            GraphUtils.writeImage(dot, "plots/ds1", filename);
        }
    }

    public static Map<String, Graph<MyVertex, MyEdgeDS2>> createGraphsFromDS2(IterableResult<Record, ParsingContext> iter) {
        Map<String, String> map = new HashMap<>();
        Map<String, Graph<MyVertex, MyEdgeDS2>> graphs = new HashMap<>();

        for (Record row : iter) {
            row.fillFieldMap(map);

            String year = map.get("year");
            Graph<MyVertex, MyEdgeDS2> graph = graphs.computeIfAbsent(year, y -> new SimpleGraph<>(MyEdgeDS2.class));

            MyVertex v1 = new MyVertex(map.get("author1"));
            MyVertex v2 = new MyVertex(map.get("author2"));
            graph.addVertex(v1);
            graph.addVertex(v2);
            try {
                graph.addEdge(v1, v2, new MyEdgeDS2(Integer.parseInt(map.get("collaborations"))));
            } catch (IllegalArgumentException ex) {
                Utils.print("self loop " + v1 + " - " + v2);
            }
        }
        return graphs;
    }

    public static void writeGraphsFromDS2(Map<String, Graph<MyVertex, MyEdgeDS2>> graphs)
            throws IOException, ExportException, URISyntaxException, TransformerException {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Graph<MyVertex, MyEdgeDS2>> entry : graphs.entrySet()) {
            String filename = entry.getKey();
            Graph<MyVertex, MyEdgeDS2> graph = entry.getValue();
            int vertSize = graph.vertexSet().size();
            int edgeSize = graph.edgeSet().size();
            Utils.print("Graph " + filename + ": vertSize " + vertSize + ", edgeSize " + edgeSize);
            File dot = GraphUtils.saveDS2Graph(graph, filename);

            if (edgeSize <= 1000) {
                try {
                    GraphUtils.writeImage(dot, "plots/ds2", filename);
                } catch (GraphvizException ge) {
                    sb.append("Something went wrong with graph " + filename + "\n");
                    print("Something went wrong with graph " + filename);
                }
            } else {
                sb.append("Graph " + filename + " is too big to be drawn with GraphViz\n");
                print("Graph " + filename + " is too big to be drawn with GraphViz");
                GraphUtils.writeImage(graph,"plots/ds2", filename);
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        String now = LocalDateTime.now().format(formatter);
        File f = GraphUtils.getNewFile(".", "log__" + now, "txt");
        try (FileWriter writer = new FileWriter(f)) {
            writer.write(sb.toString());
        }
    }
}
