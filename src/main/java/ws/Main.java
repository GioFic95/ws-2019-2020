package ws;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import org.jgrapht.Graph;
import org.jgrapht.io.ExportException;
import ws.myGraph.MyEdge;
import ws.myGraph.MyVertex;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String... args) {
        IterableResult<Record, ParsingContext> iter = Preprocessing.readDS1();
        Map<String, Graph<MyVertex, MyEdge>> graphs = Preprocessing.createGraphsFromDS1(iter);
        try {
            Preprocessing.writeGraphsFromDS1(graphs);
        } catch (ExportException | IOException e) {
            e.printStackTrace();
        }
    }
}
