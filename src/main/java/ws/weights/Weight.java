package ws.weights;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;

public abstract class Weight<V, E extends DefaultEdge> {
    protected Graph<V, E> graph;

    public Weight(Graph<V, E> graph) {
        this.graph = graph;
    }

    public abstract Map<V, Double> getScores();

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
