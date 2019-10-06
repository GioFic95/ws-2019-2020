package ws;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.ImportException;
import ws.myGraph.MyEdgeDS1;
import ws.myGraph.MyVertex;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class Main {
    private static void preprocessing() throws ExportException, IOException, URISyntaxException, TransformerException {
        // preprocess DS1
        IterableResult<Record, ParsingContext> iter1 = Preprocessing.readDS(
                new String[]{"year", "keyword1", "keyword2", "authors"},"dataset/ds-1.tsv");
        Map<String, Graph<MyVertex, MyEdgeDS1>> graphs1 = Preprocessing.createGraphsFromDS1(iter1);
        Preprocessing.writeGraphsFromDS1(graphs1);

        // preprocess DS2
        IterableResult<Record, ParsingContext> iter2 = Preprocessing.readDS(
                new String[]{"year", "author1", "author2", "collaborations"},"dataset/ds-2.tsv");
        Map<String, Graph<MyVertex, DefaultWeightedEdge>> graphs2 = Preprocessing.createGraphsFromDS2(iter2);
        Preprocessing.writeGraphsFromDS2(graphs2);
    }

    public static void main(String... args) throws ExportException, IOException, URISyntaxException, TransformerException, ImportException {
//        preprocessing();

        Task1.tryMeasures();
    }
}
