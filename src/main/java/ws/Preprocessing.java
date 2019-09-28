package ws;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static ws.Utils.*;

public class Preprocessing {

    public static void readDS1() {
        TsvParserSettings settings = new TsvParserSettings();
        settings.setHeaderExtractionEnabled(false);
        settings.setHeaders("year", "keyword1", "keyword2", "authors");
        TsvParser parser = new TsvParser(settings);
//        parser.getRecordMetadata().setTypeOfColumns(String.class, "year", "keyword1", "keyword2", "authors");

        InputStream ds1 = Preprocessing.class.getResourceAsStream("dataset/ds-1.tsv");
        IterableResult<Record, ParsingContext> iter = parser.iterateRecords(ds1);
        Map<String, String> map = new HashMap<>();
        long counter = 1;

        for (Record row : iter) {
            row.fillFieldMap(map);
            print(counter);
            print(map);
            print("");
            counter++;
        }
    }
}
