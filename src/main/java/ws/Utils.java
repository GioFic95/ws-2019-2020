package ws;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public static void writeLog(@NotNull StringBuilder sb, String fileName, boolean addDate) throws IOException, URISyntaxException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        String now = addDate ? "__" + LocalDateTime.now().format(formatter) : "";
        File f = getNewFile("logs", fileName + now, "txt");
        try (FileWriter writer = new FileWriter(f, true)) {
            writer.append(sb.toString());
        }
    }

    /**
     * todo
     * @param sb
     * @param fileName
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void writeLog(@NotNull StringBuilder sb, String fileName) throws IOException, URISyntaxException {
        writeLog(sb, fileName, true);
    }

    /**
     * A commodity function to easily parse TSV files.
     * @param headers The names of the columns to be used.
     * @param path    The path where the TSV file is located.
     * @return        An {@link IterableResult} to be used to read the rows of the TSV file.
     * @see <a href="https://www.univocity.com/pages/univocity_parsers_tsv.html" target="_blank">Univocity parsers</a>.
     */
    public static IterableResult<Record, ParsingContext> readTSV(String[] headers, String path) {
        TsvParserSettings settings = new TsvParserSettings();
        settings.setMaxCharsPerColumn(8192);
        settings.setHeaderExtractionEnabled(false);
        settings.setHeaders(headers);
        TsvParser parser = new TsvParser(settings);

        InputStream ds = Preprocessing.class.getResourceAsStream(path);
        return parser.iterateRecords(ds);
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
//        print(fileName + " " + new File(res).exists());

        String path = res.getPath() + "/" + fileName + "." + ext;
//        print(path);
        return new File(path);
    }

    public static void delMatchigFiles(String pathName, String pattern) throws URISyntaxException {
        URI res = Main.class.getResource(pathName).toURI();
        File folder = new File(res);

        final File[] files = folder.listFiles((dir, name) -> name.matches(pattern));
        if (files != null) {
            for (final File file : files) {
                if (!file.delete()) {
                    throw new IllegalStateException("Can't remove " + file.getAbsolutePath());
                }
            }
        }
    }

    public static String findLastLog(String pattern) throws URISyntaxException {
        URI res = Main.class.getResource("logs").toURI();
        File folder = new File(res);
        final File[] files = folder.listFiles((dir, name) -> name.matches(pattern));
//        print(files);
        if (files != null) {
            final List<String> names = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
            print(names);
            return Collections.max(names, Comparator.naturalOrder());
        } else {
            print("no matching files");
            return null;
        }
    }
}
