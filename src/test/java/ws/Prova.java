package ws;

import ws.Utils;
import ws.myGraph.MyVertex;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

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

        Double res = Double.POSITIVE_INFINITY;
        assert res >= 0 && res <= 1 : "caso 1";
        Utils.print(res);
        Double resneg = Double.NEGATIVE_INFINITY;
        assert resneg >= 0 && resneg <= 1 : "caso 2";
        Utils.print(resneg);
    }

}
