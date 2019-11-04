package ws.weights;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;

/**
 * A class that represents a weight that can be applied to a scoring of a graph to produce a more complex scoring.
 * Actually a {@link Weight} is a complementary scoring measure to be used together with another scoring measure.
 * This class and its subclasses are thought and designed to be used with graphs of dataset DS1.
 * @param <V> The type of the vertexes of the graph to be weighted.
 * @param <E> The type of the edges of the graph to be weighted.
 */
public abstract class Weight<V, E extends DefaultEdge> {
    protected Graph<V, E> graph;

    /**
     * Create a new weighting of the given graph.
     * @param graph The graph to be weighted.
     */
    public Weight(Graph<V, E> graph) {
        this.graph = graph;
    }

    /**
     * Computes the actual weighting or scoring of the graph, computed according to this weight.
     * @return the weighing of the graph.
     */
    public abstract Map<V, Double> getScores();

    /**
     * Composes two weights to produce a more complex weighing for the graph.
     * @param w1  The first weight.
     * @param w2  The second weight.
     * @param a   The fraction in which the first weight is considered.
     * @param b   The fraction in which the second weight is considered.
     * @param <V> The type of the vertexes of the graph.
     * @param <E> The type of the edges of the graph.
     * @return A composed weight of the graph.
     */
    public static <V, E extends DefaultEdge> Weight compose(Weight w1, Weight w2, double a, double b) {
        if (! w1.graph.equals(w2.graph)) {
            throw new IllegalArgumentException("Incompatible weights: they must refer to the same graph");
        }
        return new Weight<V, E>(w1.graph) {
            @Override
            public Map<V, Double> getScores() {
                Map<V, Double> scores = w1.getScores();
                Map<V, Double> weights = w2.getScores();
                scores.forEach((vertex, score) -> scores.put(vertex, (score * a) + (weights.get(vertex) * b)));
                return scores;
            }
        };
    }

}
