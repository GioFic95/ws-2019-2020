package ws.myGraph;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.DefaultAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that describes an edge of a graph of the dataset DS1: each edge has an attribute with the authors and the
 * number fo times they used the keywords connected by that edge.
 */
public class MyEdgeDS1 extends DefaultEdge {
    /**
     * The map of authors for this edge, organized as follows: key-value (A,N) pairs where A represents an author,
     * whereas N is the number of times A uses the pair of keywords connected by this edge in his/her articles.
     */
    private final Map<String, Integer> authors;

    /**
     * Creates an edge giving its attribute as parameter.
     * @param authors The map of authors for this edge.
     */
    public MyEdgeDS1(Map<String, Integer> authors) {
        this.authors = authors;
    }

    /**
     * Gets the map of authors for this edge, organized as follows: key-value (A,N) pairs where A represents an author,
     * whereas N is the number of times A uses the pair of keywords connected by this edge in his/her articles.
     * @return The map of authors for this edge.
     */
    public Map<String, Integer> getAuthors() {
        return authors;
    }

    /**
     * Builds a description of this edge.
     * @return A complete description of this edge.
     */
    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + ")";
    }
//    public String toString() {
//        return "(" + getSource() + " : " + getTarget() + " - " + authors + ")";
//    }

    /**
     * In order to serialize the graph in DOT format, the authors map must be converted into a map attributes.
     * @return The authors map converted into a map of pairs {@link String}-{@link Attribute}.
     * @see #getAuthorsFromAttributes(Map) getAuthorsFromAttributes
     */
    public Map<String, Attribute> getAttributes() {
        Map<String, Attribute> attributes = new HashMap<>();
        for (Map.Entry<String, Integer> entry : authors.entrySet()) {
            attributes.put("a_" + entry.getKey(), DefaultAttribute.createAttribute(entry.getValue()));
        }
        return attributes;
    }

    /**
     * In order to de-serialize the graph, from the DOT file, the authors map must be reconstructed from the map of
     * attributes.
     * @param attributes The authors map converted into a map of pairs {@link String}-{@link Attribute}, as done by
     *                   {@link #getAttributes()}.
     * @return The original authors map as described in {@link #authors}.
     * @see #getAttributes() getAttributes
     */
    public static Map<String, Integer> getAuthorsFromAttributes(Map<String, Attribute> attributes) {
        Map<String, Integer> authors = new HashMap<>();
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            authors.put(entry.getKey().substring(2), Integer.valueOf(entry.getValue().getValue()));
        }
        return authors;
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }

    @Override
    public Object getTarget() {
        return super.getTarget();
    }
}
