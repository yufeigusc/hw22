import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JsonUtil {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static CrawlStat parseCrawlStatFromJson(String fetchJsonFilePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fetchJsonFilePath));
        return gson.fromJson(br, CrawlStat.class);
    }

    public static void dumpCrawlStatToJson(CrawlStat crawlStat, String filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(crawlStat);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        }
    }
}
