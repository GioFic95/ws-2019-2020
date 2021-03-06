package ws.myGraph;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.DefaultAttribute;
import org.jgrapht.util.SupplierUtil;

import java.lang.reflect.Type;
import java.util.*;
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

    /**
     * Build a {@link Gson} object with all the required serializers and deserializers, that is,
     * {@link MyVertexSerializer}, {@link MyVertexDeserializer}, {@link MyMapOfMyVertexToMyVertexesSerializer},
     * {@link MyMapOfMyVertexesToMyVertexesSerializer}, {@link MyMapOfMyVertexesToMyVertexesDeserializer}.
     * @return The Gson object, ready to be used.
     */
    public static Gson getGson() {
        Type type1 = new TypeToken<Map<MyVertex, Set<MyVertex>>>(){}.getType();
        Type type2 = new TypeToken<Map<Set<MyVertex>, Set<MyVertex>>>(){}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MyVertex.class, new MyVertex.MyVertexDeserializer())
                .registerTypeAdapter(MyVertex.class, new MyVertex.MyVertexSerializer())
                .registerTypeAdapter(type1, new MyVertex.MyMapOfMyVertexToMyVertexesSerializer())
                .registerTypeAdapter(type2, new MyVertex.MyMapOfMyVertexesToMyVertexesSerializer())
                .registerTypeAdapter(type2, new MyVertex.MyMapOfMyVertexesToMyVertexesDeserializer())
                .create();
        return gson;
    }

    /**
     * A deserializer for {@link MyVertex}.
     */
    public static class MyVertexDeserializer implements JsonDeserializer<MyVertex> {
        @Override
        public MyVertex deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String jsonVertex = json.getAsString();
            String id = jsonVertex.split("___")[0];
            String value = jsonVertex.split("___")[1];
            return new MyVertex(id, value);
        }
    }

    /**
     * A deserializer for {@link MyVertex}.
     */
    public static class MyVertexSerializer implements JsonSerializer<MyVertex> {
        @Override
        public JsonElement serialize(MyVertex myVertex, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(myVertex.getId() + "___" + myVertex.getValue());
        }
    }

    /**
     * A serializer for maps {@link MyVertex} -> set of {@link MyVertex}.
     */
    public static class MyMapOfMyVertexToMyVertexesSerializer implements JsonSerializer<Map<MyVertex, Set<MyVertex>>> {
        @Override
        public JsonElement serialize(Map<MyVertex, Set<MyVertex>> myVertexSetMap, Type type, JsonSerializationContext jsonSerializationContext) {
//            Utils.print("MyMapOfMyVertexToMyVertexesSerializer " + myVertexSetMap);
            JsonObject jo = new JsonObject();
            myVertexSetMap.forEach((k, v) -> {
                JsonElement jek = new MyVertex.MyVertexSerializer().serialize(k, type, jsonSerializationContext);
                JsonArray ja = new JsonArray();
                v.forEach(myVertex -> {
                    JsonElement jev = new MyVertex.MyVertexSerializer().serialize(myVertex, type, jsonSerializationContext);
                    ja.add(jev);
                });
                jo.add(jek.getAsString(), ja);
            });
            return jo;
        }
    }

    /**
     * A serializer for maps set of {@link MyVertex} -> set of {@link MyVertex}.
     */
    public static class MyMapOfMyVertexesToMyVertexesSerializer implements JsonSerializer<Map<Set<MyVertex>, Set<MyVertex>>> {
        @Override
        public JsonElement serialize(Map<Set<MyVertex>, Set<MyVertex>> myVertexSetMap, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jo = new JsonObject();
            myVertexSetMap.forEach((k, v) -> {
                JsonArray jak = new JsonArray();
                k.forEach(myVertex -> {
                    JsonElement jev = new MyVertex.MyVertexSerializer().serialize(myVertex, type, jsonSerializationContext);
                    jak.add(jev);
                });
                JsonArray jav = new JsonArray();
                v.forEach(myVertex -> {
                    JsonElement jev = new MyVertex.MyVertexSerializer().serialize(myVertex, type, jsonSerializationContext);
                    jav.add(jev);
                });
                jo.add(jak.toString(), jav);
            });
            return jo;
        }
    }

    /**
     * A deserializer for maps {@link MyVertex} -> set of {@link MyVertex}.
     */
    public static class MyMapOfMyVertexesToMyVertexesDeserializer implements JsonDeserializer<Map<Set<MyVertex>, Set<MyVertex>>> {
        @Override
        public Map<Set<MyVertex>, Set<MyVertex>> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Map<Set<MyVertex>, Set<MyVertex>> map = new HashMap<>();
            JsonObject jo = jsonElement.getAsJsonObject();
            jo.entrySet().forEach(jentry -> {
                Set<MyVertex> setk = new HashSet<>();
                Set<MyVertex> setv = new HashSet<>();

                String s = jentry.getKey();
                s = s.substring(1, s.length()-1);
                String[] ss = s.split(",");
                for (String mv : ss) {
                    mv = mv.substring(1, mv.length()-1);
                    setk.add(new MyVertex.MyVertexDeserializer().deserialize(new JsonPrimitive(mv), type, jsonDeserializationContext));
                }

                JsonArray ja = jentry.getValue().getAsJsonArray();
                ja.forEach(e -> setv.add(new MyVertex.MyVertexDeserializer().deserialize(e, type, jsonDeserializationContext)));
                map.put(setk, setv);
            });
            return map;
        }
    }
}
