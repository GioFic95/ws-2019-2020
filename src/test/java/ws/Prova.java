package ws;

import ws.Utils;
import ws.myGraph.MyVertex;

import java.util.*;

public class Prova {
    public static void main(String... args) {
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

        MyVertex v1 = new MyVertex("v1");
        MyVertex v2 = new MyVertex("v2");
        MyVertex v3 = new MyVertex("v3");
        MyVertex v4 = new MyVertex("v4");
        Set<MyVertex> s = new HashSet<>();
        s.add(v1);
        s.add(v2);
        s.add(v3);
        s.add(v4);
        Utils.print(s);
        MyVertex v4bis = new MyVertex("v4");
        if (! s.contains(v4bis)) {
            Utils.print(v4bis);
        }
        s.add(v4bis);
        Utils.print(s);
    }

}
