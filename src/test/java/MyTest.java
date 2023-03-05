import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MyTest {
    private static final Pattern FILTER = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    private static final Pattern NO_DOT = Pattern.compile("^[^.]*$");

    static String regex = ".*\\.(css|js|bmp|ico|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|ram|m4v|mkv|ogg|ogv|ps|eps|tex|ppt|pptx|xls|xlsx|names|data|dat|exe|bz2|tar|msi|bin|7z|psd|dmg|iso|epub|dll|cnf|tgz|sha1|thmx|mso|arff|rtf|jar|csv|rm|smil|wmv|swf|wma|zip|rar|gz)$";
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(regex);
    public static void main(String[] args) throws IOException {
        List<FetchUrl> fetchUrls = new ArrayList<>();
// populate fetchUrls

        List<VisitedUrl> visitedUrls = new ArrayList<>();
// populate visitedUrls

        List<DiscoveredUrl> discoveredUrls = new ArrayList<>();
// populate discoveredUrls

        fetchUrls.add(new FetchUrl("https://www.latimes.com/", 200, "OK"));
        fetchUrls.add(new FetchUrl("https://www.latimes.com/", 201, "OK"));
        visitedUrls.add(new VisitedUrl("https://www.latimes.com/", 200, 5, "text/html"));
        visitedUrls.add(new VisitedUrl("https://www.latimes.com/", 201, 5, "text/html"));
        discoveredUrls.add(new DiscoveredUrl("https://www.latimes.com/", "OK"));
        discoveredUrls.add(new DiscoveredUrl("https://www.latimes.com/", "N_OK"));

        CrawlStat crawlStat = new CrawlStat();
        crawlStat.addAllVisitedUrls(visitedUrls);
        crawlStat.addAllDiscoveredUrls(discoveredUrls);
        crawlStat.addAllFetchUrls(fetchUrls);

//        JsonUtil.dumpFetchUrls(fetchUrls, "fetchUrls.json");
//        JsonUtil.dumpVisitedUrls(visitedUrls, "visitedUrls.json");
//        JsonUtil.dumpDiscoveredUrls(discoveredUrls, "discoveredUrls.json");
//
//        JsonUtil.parseFetchUrls("fetchUrls.json");
//        System.out.println("==================================");
        JsonUtil.dumpCrawlStatToJson(crawlStat, "crawlStat.json");
        CrawlStat crawlStat1 = JsonUtil.parseCrawlStatFromJson("crawlStat.json");
        System.out.println(crawlStat1);

    }
}
