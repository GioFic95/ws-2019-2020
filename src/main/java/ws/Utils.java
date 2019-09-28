package ws;

import java.util.Arrays;
import java.util.List;

public class Utils {
    public static void print(Object x) {
        if (x == null) {
            throw new IllegalArgumentException();
        }
        if (x.getClass().isArray()) {
            System.out.println("ARRAY!");
            List<Object> l = Arrays.asList((Object[]) x);
            System.out.println(l);
        } else {
            System.out.println(x);
        }
    }

    public static void printList(Iterable iter) {
        for (Object o : iter) {
            System.out.println(o);
        }
    }
}
