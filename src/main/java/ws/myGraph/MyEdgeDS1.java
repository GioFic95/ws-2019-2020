package ws.myGraph;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.DefaultAttribute;

import java.util.HashMap;
import java.util.Map;


public class MyEdgeDS1 extends DefaultEdge {
    private final Map<String, Integer> authors;

    public MyEdgeDS1(Map<String, Integer> authors) {
        this.authors = authors;
    }

    public Map<String, Integer> getAuthors() {
        return authors;
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " - " + authors + ")";
    }

    public Map<String, Attribute> getAttributes() {
        Map<String, Attribute> attributes = new HashMap<>();
        for (Map.Entry<String, Integer> entry : authors.entrySet()) {
            attributes.put("a_" + entry.getKey(), DefaultAttribute.createAttribute(entry.getValue()));
        }
        return attributes;
    }

    public static Map<String, Integer> getAuthorsFromAttributes(Map<String, Attribute> attributes) {
        Map<String, Integer> authors = new HashMap<>();
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            authors.put(entry.getKey().substring(2), Integer.valueOf(entry.getValue().getValue()));
        }
        return authors;
    }
}
