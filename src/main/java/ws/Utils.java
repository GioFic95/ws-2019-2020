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

/**
 * A class with general purpose utility functions.
 */
public class Utils {

    /**
     * A commodity function to print objects faster.
     * @param x The object to print.
     */
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

    /**
     * A commodity function to print iterable objects faster, with one element per row.
     * @param iter The iterable object to print.
     */
    public static void printList(@NotNull Iterable iter) {
        for (Object o : iter) {
            System.out.println(o);
        }
    }

    /**
     * A commodity function to print simple log files with the given content, and with a name consisting of the given
     * name plus the current timestamp.
     * @param sb       The content to be written into the log file.
     * @param fileName The name to be assigned to the log file, together with the current timestamp.
     * @throws IOException if it can't write to the created log file.
     * @throws URISyntaxException if raised by {@link #getNewFile(String, String, String)}).
     */
    public static void writeLog(@NotNull StringBuilder sb, String fileName) throws IOException, URISyntaxException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        String now = LocalDateTime.now().format(formatter);
        File f = getNewFile("logs", fileName + "__" + now, "txt");
        try (FileWriter writer = new FileWriter(f)) {
            writer.write(sb.toString());
        }
    }

    /**
     * Create a new {@link File} with the given path, name and extension, into the resources directory.
     * @param pathName The path in which the file is to be created. It bust be an existing subdirectory of the
     *                 resources directory.
     * @param fileName The name to be used for the file.
     * @param ext      The extension of the file.
     * @return The file object created with the given parameters.
     * @throws URISyntaxException if it can't build a valid {@link java.net.URI} from the given file name.
     */
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
