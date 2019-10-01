package ws;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.ExportException;
import org.json.JSONObject;
import ws.myGraph.GraphUtils;
import ws.myGraph.MyEdge;
import ws.myGraph.MyVertex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static ws.Utils.*;

public class Preprocessing {

    public static IterableResult<Record, ParsingContext> readDS1() {
        TsvParserSettings settings = new TsvParserSettings();
        settings.setHeaderExtractionEnabled(false);
        settings.setHeaders("year", "keyword1", "keyword2", "authors");
        TsvParser parser = new TsvParser(settings);

        InputStream ds1 = Preprocessing.class.getResourceAsStream("dataset/ds-1.tsv");
        IterableResult<Record, ParsingContext> iter = parser.iterateRecords(ds1);

//        Map<String, String> map = new HashMap<>();
//        long counter = 1;
//        for (Record row : iter) {
//            row.fillFieldMap(map);
//            print(counter);
//            print(map);
//            print("");
//            counter++;
//        }

        return iter;
    }

    public static Map<String, Graph<MyVertex, MyEdge>> createGraphsFromDS1(IterableResult<Record, ParsingContext> iter) {
        Map<String, String> map = new HashMap<>();
        Map<String, Graph<MyVertex, MyEdge>> graphs = new HashMap<>();

        for (Record row : iter) {
            row.fillFieldMap(map);
            print(map);

            if (map.get("keyword1").contains("??") || map.get("keyword2").contains("??"))
                continue;

            String year = map.get("year");
            Graph<MyVertex, MyEdge> myGraph = graphs.computeIfAbsent(year, y -> new SimpleGraph<>(MyEdge.class));

            MyVertex v1 = new MyVertex(map.get("keyword1"));
            MyVertex v2 = new MyVertex(map.get("keyword2"));
            myGraph.addVertex(v1);
            myGraph.addVertex(v2);

            Map<String, Integer> authors = new HashMap<>();
            JSONObject jAuthors = new JSONObject(map.get("authors"));
            for (String k : jAuthors.keySet()) {
                authors.put(k, jAuthors.getInt(k));
            }
            myGraph.addEdge(v1, v2, new MyEdge(authors));
        }
        return graphs;
    }

    public static void writeGraphsFromDS1(Map<String, Graph<MyVertex, MyEdge>> graphs) throws ExportException, IOException {
        for (Map.Entry<String, Graph<MyVertex, MyEdge>> entry : graphs.entrySet()) {
            String filename = entry.getKey();
            Graph<MyVertex, MyEdge> myGraph = entry.getValue();
            int vertSize = myGraph.vertexSet().size();
            int edgeSize = myGraph.edgeSet().size();
            Utils.print("Graph " + filename + ": vertSize " + vertSize + ", edgeSize " + edgeSize);
            File dot = GraphUtils.saveMyGraph(myGraph, filename);
            GraphUtils.writeImage(dot, filename);
        }
    }
}
