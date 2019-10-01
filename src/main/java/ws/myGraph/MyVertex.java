package ws.myGraph;

import org.jgrapht.io.Attribute;
import org.jgrapht.io.DefaultAttribute;
import org.jgrapht.util.SupplierUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class MyVertex {
    private final String id;
    private final String value;
    private final static Supplier<Long> idSupplier = SupplierUtil.createLongSupplier(1);

    public MyVertex(String value) {
        this.value = value;
        this.id = idSupplier.get().toString();
    }

    MyVertex(String id, String value) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyVertex myVertex = (MyVertex) o;
        return value.equals(myVertex.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
