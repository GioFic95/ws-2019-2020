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
import ws.task2.Task2;
import ws.utils.PrettyPrint;
import ws.utils.Utils;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static ws.utils.Utils.*;

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
        IterableResult<Record, ParsingContext> iter1 = readTSV(
                new String[]{"year", "keyword1", "keyword2", "authors"},"dataset/ds-1.tsv");
        Map<String, Graph<MyVertex, MyEdgeDS1>> graphs1 = Preprocessing.createGraphsFromDS1(iter1);
        Preprocessing.writeGraphsFromDS1(graphs1);

        // preprocess DS2
        IterableResult<Record, ParsingContext> iter2 = readTSV(
                new String[]{"year", "author1", "author2", "collaborations"},"dataset/ds-2.tsv");
        Map<String, Graph<MyVertex, DefaultWeightedEdge>> graphs2 = Preprocessing.createGraphsFromDS2(iter2);
        Preprocessing.writeGraphsFromDS2(graphs2);
    }

    /**
     * Write human-readable logs by calling {@link PrettyPrint#topK()}, {@link PrettyPrint#spreadOfInfluence()},
     * {@link PrettyPrint#merge()} and {@link PrettyPrint#tracing()}.
     * @throws IOException if raised by any of the called methods.
     * @throws URISyntaxException if raised by any of the called methods.
     * @throws ImportException if raised by {@link PrettyPrint#topK()} or {@link PrettyPrint#spreadOfInfluence()}.
     */
    private static void humanReadableOutput() throws IOException, URISyntaxException, ImportException {
        PrettyPrint.topK();
        PrettyPrint.spreadOfInfluence();
        PrettyPrint.merge();
        PrettyPrint.tracing();
    }

    /**
     * Execute all the steps of the preprocessing phase by calling {@link #preprocessing}, then all the steps of the
     * first task by calling {@link Task1#tryMeasures} and {@link Task1#multipleIndependentCascadeFlow)},
     * and the steps in the second task by calling {@link Task2#traceTopics}.
     * Finally, read the last outputs of the various steps and writes more human-readable logs,
     * by calling{@link #humanReadableOutput}.
     * @param args Not needed.
     * @throws ExportException if raised by {@link #preprocessing}.
     * @throws TransformerException if raised by {@link #preprocessing}.
     * @throws IOException if raised by any of the called methods.
     * @throws URISyntaxException if raised by any of the called methods.
     * @throws ImportException if raised by any of the called methods.
     */
    public static void main(String... args) throws ExportException, IOException, URISyntaxException, TransformerException, ImportException {
        printNow();
        preprocessing();
        printNow();
        Task1.tryMeasures();
        printNow();
        /*Task1.multipleIndependentCascadeFlow(30, 1.5, 0.3, 0.5, true, "15_");
        printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.3, 0.5, true, "1_");
        printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.3, 0.7, false, "2_");
        printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.3, 0.8, false, "4_");
        printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.3, 0.8, false, "5_");
        printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.2, 0.8, false, "6_");
        printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.2, 0.7, false, "7_");
        printNow();
        Task1.multipleIndependentCascadeFlow(30, 2, 0.1, 0.4, false, "8_");
        printNow();
        Task1.multipleIndependentCascadeFlow(30, 1, 0.1, 0.4, false, "9_");
        printNow();*/
        Task1.multipleIndependentCascadeFlow(30, 1, 0.1, 0.4, true, "10_");
        printNow();
        Task2.traceTopics("", 0.7);
        printNow();
        humanReadableOutput();
    }
}
