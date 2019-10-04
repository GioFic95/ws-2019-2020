package ws;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static void print(Object x) {
        if (x == null) {
            System.out.println("null");
        } else if (x.getClass().isArray()) {
            System.out.println("ARRAY!");
            List<Object> l = Arrays.asList((Object[]) x);
            System.out.println(l);
        } else {
            System.out.println(x);
        }
    }

    public static void printList(@NotNull Iterable iter) {
        for (Object o : iter) {
            System.out.println(o);
        }
    }

    public static void writeLog(@NotNull StringBuilder sb, String fileName) throws IOException, URISyntaxException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        String now = LocalDateTime.now().format(formatter);
        File f = getNewFile("logs", fileName + "__" + now, "txt");
        try (FileWriter writer = new FileWriter(f)) {
            writer.write(sb.toString());
        }
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static File getNewFile(String pathName, String fileName, String ext) throws URISyntaxException {
        URI res = Main.class.getResource(pathName).toURI();
        System.out.println(fileName + " " + new File(res).exists());

        String path = res.getPath() + "/" + fileName + "." + ext;
        System.out.println(path);
        return new File(path);
    }
}
