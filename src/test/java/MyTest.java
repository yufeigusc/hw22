import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class MyTest {
    private static final Pattern FILTER = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    private static final Pattern NO_DOT = Pattern.compile("^[^.]*$");

    static String regex = ".*\\.(css|js|bmp|ico|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|ram|m4v|mkv|ogg|ogv|ps|eps|tex|ppt|pptx|xls|xlsx|names|data|dat|exe|bz2|tar|msi|bin|7z|psd|dmg|iso|epub|dll|cnf|tgz|sha1|thmx|mso|arff|rtf|jar|csv|rm|smil|wmv|swf|wma|zip|rar|gz)$";
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(regex);
    public static void main(String[] args) throws IOException {
        CrawlStat crawlStat = JsonUtil.parseCrawlStatFromJson("crawlStat_latimes.json");
//        List<VisitedUrl> visitedUrls = crawlStat.getVisitedUrls();
//        HashMap<String, Integer> content_map = new HashMap<>();
//        for (VisitedUrl visitedUrl : visitedUrls) {
//            String content_type = visitedUrl.getContentType();
//            if (content_type != null) {
//                if (content_map.containsKey(content_type)) {
//                    content_map.put(content_type, content_map.get(content_type) + 1);
//                } else {
//                    content_map.put(content_type, 1);
//                }
//            }
//        }
//        StringBuilder content_sb = new StringBuilder();
//        content_map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(
//                entry -> content_sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n")
//        );
//        System.out.println(
//                "Content Types:\n" +
//                        "==============\n" +
//                        content_sb.toString() +
//                        "\n"
//        );
        MyCrawlerController.setCrawlStat(crawlStat);
        MyCrawlerController.getStat();
    }
}
