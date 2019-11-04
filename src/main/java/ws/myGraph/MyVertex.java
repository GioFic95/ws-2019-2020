package ws.myGraph;

import org.jgrapht.io.Attribute;
import org.jgrapht.io.DefaultAttribute;
import org.jgrapht.util.SupplierUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 *  A class that describes a vertex of a graph of the dataset DS1 or DS2. Each node has an ID, assigned incrementally,
 *  and a value, that is the actual content of the node.
 */
public class MyVertex {
    /**
     * The ID of the node.
     */
    private final String id;

    /**
     * The content of the node.
     */
    private final String value;

    /**
     * A supplier that produce sequential IDs for the nodes.
     */
    private final static Supplier<Long> idSupplier = SupplierUtil.createLongSupplier(1);

    /**
     * Creates a vertex giving its value as parameter.
     * @param value The content of the node. For example, a keyword for a node in DS1 or an author for a node in DS2.
     */
    public MyVertex(String value) {
        this.value = value;
        this.id = idSupplier.get().toString();
    }

    /**
     * Creates a vertex giving its ID and its value as parameters.
     * @param id    The explicit ID to be assigned to this vertex. Note that if it isn't unique in a graph, the node
     *              will not be added to the graph.
     * @param value The content of the node. For example, a keyword for a node in DS1 or an author for a node in DS2.
     */
    MyVertex(String id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * Gets the ID of the node.
     * @return The ID of the node.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the content of the node. For example, a keyword for a node in DS1 or an author for a node in DS2.
     * @return The value of the node.
     */
    public String getValue() {
        return value;
    }

    /**
     * In order to serialize the graph in DOT format, the value of the node must be converted into an {@link Attribute}
     * of the vertex.
     * @return A map with a single {@link String}-{@link Attribute} pair: "value"-&#60;value of the node&#62;.
     */
    public Map<String, Attribute> getAttribute() {
        Map<String, Attribute> attribute = new HashMap<>();
        attribute.put("value", DefaultAttribute.createAttribute(getValue()));
        return attribute;
    }

    /**
     * Builds a description of this vertex.
     * @return A complete description of this vertex.
     */
    @Override
    public String toString() {
        return id + ": " + value;
    }

    /**
     * Compare two vertexes and decides that they are equal if they have the same value.
     * @param o A vertex to be compared with this one.
     * @return True if the two nodes have the same value.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyVertex myVertex = (MyVertex) o;
        return value.equals(myVertex.value);
    }

    /**
     * Computes the hash code of this vertex based on its value.
     * @return The hash code of this vertex.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
