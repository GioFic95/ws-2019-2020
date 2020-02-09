package ws;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ws.Utils;
import ws.myGraph.MyVertex;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Prova {
    public static void main(String... args) throws URISyntaxException, IOException {
        // do tests

//        String[] array = new String[] {"a","2","4"};
//        Utils.print(array);
//        Utils.print("ciao");
//        Utils.print(Arrays.asList(array));
//        Utils.printList(Arrays.asList(array));

//        try {
//            GraphUtils.demo();
//        } catch (ExportException | ImportException | IOException e) {
//            e.printStackTrace();
//        }

//        MyVertex v1 = new MyVertex("v1");
//        MyVertex v2 = new MyVertex("v2");
//        MyVertex v3 = new MyVertex("v3");
//        MyVertex v4 = new MyVertex("v4");
//        Set<MyVertex> s = new HashSet<>();
//        s.add(v1);
//        s.add(v2);
//        s.add(v3);
//        s.add(v4);
//        Utils.print(s);
//        MyVertex v4bis = new MyVertex("v4");
//        if (! s.contains(v4bis)) {
//            Utils.print(v4bis);
//        }
//        s.add(v4bis);
//        Utils.print(s);

//        Utils.writeLog(new StringBuilder("prova prova"), "prova");

//        Map<String, Long> map = new HashMap<>();
//        map.put("ciao", 7L);
//        map.put("a", 1L);
//        Long max = Collections.max(map.values())/2;
//        Utils.print(max);
//
//        Map<String, Double> mapd = new HashMap<>();
//        mapd.put("ciao", 7.0);
//        mapd.put("a", 1.0);
//        Double maxd = Collections.max(mapd.values())/2;
//        Utils.print(maxd);

//        Double res = Double.POSITIVE_INFINITY;
//        assert res >= 0 && res <= 1 : "caso 1";
//        Utils.print(res);
//        Double resneg = Double.NEGATIVE_INFINITY;
//        assert resneg >= 0 && resneg <= 1 : "caso 2";
//        Utils.print(resneg);

//        List<String> dates = Arrays.asList("pag_weighted__2019_11_15__09_53_39.txt", "pag_weighted__2019_11_15__10_11_50.txt", "pag_weighted__2019_11_15__09_57_19.txt");
//        String max = Collections.max(dates, Comparator.naturalOrder());
//        Utils.print(max);

        /*Map<String, Integer> map = new HashMap<>();
        map.put("uno1", 1);
        map.put("due22", 2);
        map.put("tre333", 3);
        Utils.print(map);
        Map<String, Integer> map2 = new HashMap<>(map);
        map2.put("due", 22);
        Utils.print(map2);
        Utils.print(map);

        Utils.print(map.putIfAbsent("quattro", 4));
        Utils.print(map.get("quattro"));
        Utils.print(map.putIfAbsent("tre", 4));

        Utils.print(map);
        Utils.print("compute " + map.compute("quattro", (s, integer) -> {
            Utils.print("s " + s);
            Utils.print("int " + integer);
            if (integer != null)
                return integer+1;
            else return 1;
        }));*/

        /*Utils.print(map);
        Map<Integer, String> map3 = map.entrySet().stream().collect(Collectors.toMap(
                stringIntegerEntry -> stringIntegerEntry.getKey().length(),
                stringIntegerEntry -> String.valueOf(stringIntegerEntry.getValue())));
        Utils.print(map3);
        String s = new Gson().toJson(map3);
        Utils.print(s);
        Type type = new TypeToken<Map<Integer, String>>(){}.getType();
        Map<Integer, String> obj = new Gson().fromJson(s, type);
        Utils.print(obj);*/

/*        Utils.print(map);
        Utils.print("merge " + map.merge("quattro", 1, Integer::sum));
        Utils.print(map);
        Utils.print("merge " + map.merge("cinquemila", 1, Integer::sum));
        Utils.print(map);

        HashMap<String, Integer> map3 = new HashMap<>();
        String[] ss = {"uno", "due", "tre", "uno", "sette", "due", "uno"};
        for (String s : ss) {
            map3.merge(s, 1, Integer::sum);
        }
        Utils.print(map3);

        HashMap<MyVertex, Integer> map4 = new HashMap<>();
        MyVertex[] mvs = {v1, v2, v3, v2, v4, v1, v2};
        for (MyVertex mv : mvs) {
            map4.merge(mv, 1, Integer::sum);
        }
        Utils.print(map4);*/

        /*Set<String> s = new HashSet<>(Arrays.asList("a", "b", "c"));
        Iterator<String> it = s.iterator();
        it.forEachRemaining(ss -> {
            Utils.print(ss);
            it.remove();
        });
        Utils.print(s);*/

        Map<String, Integer> mmm = new HashMap<>(Map.of("1", 1, "2", 2, "3", 3));
        mmm.merge("2", 5, (integer, integer2) -> {
            Utils.print(integer);
            Utils.print(integer2);
            return integer*integer2;
        });
        Utils.print(mmm);
    }
}
