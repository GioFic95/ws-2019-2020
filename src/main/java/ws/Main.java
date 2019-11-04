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

    /**
     * Execute all the preprocessing steps.
     * It calls {@link Preprocessing#readDS(String[], String)} on the first dataset,
     * then {@link Preprocessing#createGraphsFromDS1(IterableResult)}.
     * It calls {@link Preprocessing#readDS(String[], String)} on the second dataset,
     * then {@link Preprocessing#createGraphsFromDS2(IterableResult)}.
     * @throws ExportException if raised by {@link Preprocessing#writeGraphsFromDS1(Map)} or {@link Preprocessing#writeGraphsFromDS2(Map)}.
     * @throws IOException if raised by {@link Preprocessing#writeGraphsFromDS1(Map)} or {@link Preprocessing#writeGraphsFromDS2(Map)}.
     * @throws URISyntaxException if raised by {@link Preprocessing#writeGraphsFromDS1(Map)} or {@link Preprocessing#writeGraphsFromDS2(Map)}.
     * @throws TransformerException if raised by {@link Preprocessing#writeGraphsFromDS2(Map)}.
     */
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

    /**
     * Execute all the steps of the preprocessing phase by calling {@link #preprocessing()}, then all the steps of the
     * first task by calling {@link Task1#tryMeasures()}.
     * @param args Not needed.
     * @throws ExportException if raised by {@link #preprocessing()}.
     * @throws TransformerException if raised by {@link #preprocessing()}.
     * @throws IOException if raised by {@link Task1#tryMeasures()} or by {@link #preprocessing()}.
     * @throws URISyntaxException if raised by {@link Task1#tryMeasures()} or by {@link #preprocessing()}.
     * @throws ImportException if raised by {@link Task1#tryMeasures()}.
     */
    public static void main(String... args) throws ExportException, IOException, URISyntaxException, TransformerException, ImportException {
        preprocessing();

        Task1.tryMeasures();
    }
}
