package ws.myGraph;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.DefaultAttribute;

import java.util.HashMap;
import java.util.Map;


public class MyEdge extends DefaultEdge {
    private Map<String, Integer> authors;

    public MyEdge(Map<String, Integer> authors) {
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
            attributes.put(entry.getKey(), DefaultAttribute.createAttribute(entry.getValue()));
        }
        return attributes;
    }

    public static Map<String, Integer> getAuthorsFromAttributes(Map<String, Attribute> attributes) {
        Map<String, Integer> authors = new HashMap<>();
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            authors.put(entry.getKey(), Integer.valueOf(entry.getValue().getValue()));
        }
        return authors;
    }
}
