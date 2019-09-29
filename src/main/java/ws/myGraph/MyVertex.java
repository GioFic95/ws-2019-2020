package ws.myGraph;

import org.jgrapht.io.Attribute;
import org.jgrapht.io.DefaultAttribute;
import org.jgrapht.util.SupplierUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MyVertex {
    private String id;
    private String value;
    private static Supplier<Long> idSupplier = SupplierUtil.createLongSupplier(1);

    public MyVertex(String value) {
        this.value = value;
        this.id = idSupplier.get().toString();
    }

    public MyVertex(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Map<String, Attribute> getAttribute() {
        Map<String, Attribute> attribute = new HashMap<>();
        attribute.put("value", DefaultAttribute.createAttribute(getValue()));
        return attribute;
    }

    @Override
    public String toString() {
        return id + ": " + value;
    }
}
