package ws.myGraph;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.DefaultAttribute;

import java.util.HashMap;
import java.util.Map;


public class MyEdgeDS2 extends DefaultEdge {
    private final int collaborations;

    public MyEdgeDS2(int collaborations) {
        this.collaborations = collaborations;
    }

    public int getCollaborations() {
        return collaborations;
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " - " + collaborations + ")";
    }

    public Map<String, Attribute> getAttributes() {
        Map<String, Attribute> attributes = new HashMap<>();
        attributes.put("collaborations", DefaultAttribute.createAttribute(collaborations));
        return attributes;
    }

    public static int getCollaborationsFromAttributes(Map<String, Attribute> attributes) {
        return Integer.parseInt(attributes.get("collaborations").getValue());
    }
}
