package ws.utils;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import guru.nidi.graphviz.attribute.Color;
import ws.Main;
import ws.Preprocessing;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * A class with general purpose utility functions.
 */
public class Utils {

    /**
     * A commodity function to print simple log files with the given content, and with a name consisting of the given
     * name plus the current timestamp.
     * @param sb       The content to be written into the log file.
     * @param fileName The name to be assigned to the log file, together with the current timestamp.
     * @param addDate If true, create a new file with the current timestamp, otherwise, add content to a pre-existent file.
     * @throws IOException if it can't write to the created log file.
     * @throws URISyntaxException if raised by {@link #getNewFile(String, String, String)}).
     */
    public static void writeLog(StringBuilder sb, String fileName, boolean addDate) throws IOException, URISyntaxException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        String now = addDate ? "__" + LocalDateTime.now().format(formatter) : "";
        File f = getNewFile("logs", fileName + now, "txt");
        try (FileWriter writer = new FileWriter(f, true)) {
            writer.append(sb.toString());
        }
    }

    /**
     * A shortcut for {@link #writeLog(StringBuilder, String, boolean)} when the date is needed, since it's the
     * usual condition.
     * @param sb       The content to be written into the log file.
     * @param fileName The name to be assigned to the log file, together with the current timestamp.
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void writeLog(StringBuilder sb, String fileName) throws IOException, URISyntaxException {
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
        settings.setMaxCharsPerColumn(100_000);
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
    public static File getNewFile(String pathName, String fileName, String ext) throws URISyntaxException {
        URI res;
        try {
            res = Main.class.getResource(pathName).toURI();
        } catch (NullPointerException ex) {
            throw new NullPointerException("The selected directory " + pathName + " does not exist.");
        }

        String path = res.getPath() + "/" + fileName + "." + ext;
        return new File(path);
    }

    /**
     * A commodity function to easily delete useless files, such as old logs or plots.
     * @param pathName The path of the directory that contains the files to be removed.
     * @param pattern  The pattern with which the files to be removed must match.
     * @throws URISyntaxException if the given path can't be found and converted to URI.
     */
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

    /**
     * Find the last n logs that match with the given pattern. Useful if we want to consider some logs regarding a
     * specific part of the system.
     * @param pattern The pattern with which the files to be retrieved must match.
     * @param n       The number of logs to be retrieved.
     * @return The names of the n most recent log files matching with the given pattern.
     * @throws URISyntaxException if the log directory can't be found and converted to URI.
     */
    public static List<String> findLastLogs(String pattern, int n) throws URISyntaxException {
        URI res = Main.class.getResource("logs").toURI();
        File folder = new File(res);
        final File[] files = folder.listFiles((dir, name) -> name.matches(pattern));
        if (files != null) {
            final List<String> names = Arrays.stream(files).map(File::getName).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
            print("matching files: " + names);
            return names.subList(Math.max(names.size() - n, 0), names.size());
        } else {
            print("no matching files");
            return new ArrayList<>();
        }
    }

    /**
     * A shortcut for {@link #findLastLogs(String, int)}, useful if we're only interested in the most recent log.
     * @param pattern The pattern with which the files to be retrieved must match.
     * @return The name of the most recent log file matching with the given pattern.
     * @throws URISyntaxException if the log directory can't be found and converted to URI.
     */
    public static String findLastLog(String pattern) throws URISyntaxException {
        return findLastLogs(pattern, 1).get(0);
    }

    /**
     * A commodity function to easily and nicely print the current timestamp.
     */
    public static void printNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
        String now = LocalDateTime.now().format(formatter);
        print(now);
    }

    /**
     * A commodity function to compute a random RGB color.
     * @return a random color.
     */
    public static Color getRandColor() {
        int red = ThreadLocalRandom.current().nextInt(50, 156);
        int green = ThreadLocalRandom.current().nextInt(50, 156);
        int blue = ThreadLocalRandom.current().nextInt(50, 156);
        return Color.rgb(red, green, blue);
    }

    /**
     * A commodity function to print objects faster.
     * @param x The object to print.
     */
    public static void print(Object x) {
        if (x == null) {
            System.out.println("null");
        } else if (x.getClass().isArray()) {
            List<Object> l = Arrays.asList((Object[]) x);
            System.out.println(l);
        } else {
            System.out.println(x);
        }
    }
}
