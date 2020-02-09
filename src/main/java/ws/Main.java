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
import ws.task1.Task1;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class Main {

    /**
     * Execute all the preprocessing steps.
     * It calls {@link Utils#readTSV(String[], String)} on the first dataset,
     * then {@link Preprocessing#createGraphsFromDS1(IterableResult)}.
     * It calls {@link Utils#readTSV(String[], String)} on the second dataset,
     * then {@link Preprocessing#createGraphsFromDS2(IterableResult)}.
     * @throws ExportException if raised by {@link Preprocessing#writeGraphsFromDS1(Map)} or {@link Preprocessing#writeGraphsFromDS2(Map)}.
     * @throws IOException if raised by {@link Preprocessing#writeGraphsFromDS1(Map)} or {@link Preprocessing#writeGraphsFromDS2(Map)}.
     * @throws URISyntaxException if raised by {@link Preprocessing#writeGraphsFromDS1(Map)} or {@link Preprocessing#writeGraphsFromDS2(Map)}.
     * @throws TransformerException if raised by {@link Preprocessing#writeGraphsFromDS2(Map)}.
     */
    private static void preprocessing() throws ExportException, IOException, URISyntaxException, TransformerException {
        // preprocess DS1
        IterableResult<Record, ParsingContext> iter1 = Utils.readTSV(
                new String[]{"year", "keyword1", "keyword2", "authors"},"dataset/ds-1.tsv");
        Map<String, Graph<MyVertex, MyEdgeDS1>> graphs1 = Preprocessing.createGraphsFromDS1(iter1);
        Preprocessing.writeGraphsFromDS1(graphs1);

        // preprocess DS2
        IterableResult<Record, ParsingContext> iter2 = Utils.readTSV(
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
//        Utils.printNow();
//        preprocessing();
//        Utils.printNow();
//        Task1.tryMeasures();
//        Utils.printNow();
        /*Task1.multipleIndependentCascadeFlow(30, 1.5, 0.3, 0.5, true, "15_");
        Utils.printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.3, 0.5, true, "1_");
        Utils.printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.3, 0.7, false, "2_");
        Utils.printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.3, 0.8, false, "4_");
        Utils.printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.3, 0.8, false, "5_");
        Utils.printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.2, 0.8, false, "6_");
        Utils.printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.2, 0.7, false, "7_");*/
        Utils.printNow();
        Task1.multipleIndependentCascadeFlow(30, 2, 0.1, 0.4, false, "8_");
        Utils.printNow();

    }
}
