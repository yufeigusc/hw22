
// simple web-crawling demo :)
/*
A mashup of code in the wild (eg. from the crawler4j repo etc.) and a bit of my own...

This is a minimal example, comprised of just 2 source files, 2 libs/ .jar files...
*/

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class MyCrawlerController {

    public static CrawlStat resultStat;

    public static int numThread = 64;
    public static int pageToFetch = 20000;    // 20000

    public static int delay = 1000;

    public static void main(String[] args) throws Exception {
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder("results");

        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests).
        config.setPolitenessDelay(delay);

        config.setMaxDepthOfCrawling(16);

        config.setMaxPagesToFetch(pageToFetch);

        config.setMaxDownloadSize(Integer.MAX_VALUE);

        config.setIncludeBinaryContentInCrawling(true);

        config.setResumableCrawling(false);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);


        // STARTER 'seed'      
        controller.addSeed("https://www.latimes.com");


        // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
        // speed depends on many other factors as well. You can experiment with this to figure out what number of
        // threads works best for you.
        int numberOfCrawlers = numThread;

        // To demonstrate an example of how you can pass objects to crawlers, we use an AtomicInteger that crawlers
        // increment whenever they see a url which points to an image.
        //AtomicInteger numSeenImages = new AtomicInteger();

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        //controller.start(factory, numberOfCrawlers);
        // GO!!!!
        controller.start(MyCrawler.class, numberOfCrawlers);

        resultStat = new CrawlStat();

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();

        for (Object localData : crawlersLocalData) {
            CrawlStat stat = (CrawlStat) localData;
            resultStat.addAllFetchUrls(stat.getFetchUrls());
            resultStat.addAllDiscoveredUrls(stat.getDiscoveredUrls());
            resultStat.addAllVisitedUrls(stat.getVisitedUrls());
        }

        dumpData();
        getStat();
        JsonUtil.dumpCrawlStatToJson(resultStat, "CrawlStat_latimes.json");

    }

    public static void dumpData() throws IOException {
        File file = new File("fetch_latimes.csv");
        file.delete();
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file, true));
        writer.append("URL, Status Code\n");

        for (FetchUrl fetchUrl : resultStat.getFetchUrls()) {
            writer.append(fetchUrl.getUrl()).append(",").append(String.valueOf(fetchUrl.getStatusCode())).append("\n");
        }
        writer.close();

        file = new File("visit_latimes.csv");
        file.delete();
        file.createNewFile();
        writer = new BufferedWriter(new java.io.FileWriter(file, true));
        writer.append("Downloaded URL, Size of File, # of Outlinks, Content Type\n");

        // for each
        for (VisitedUrl visitedUrl : resultStat.getVisitedUrls()) {
            writer.append(visitedUrl.getUrl()).append(",").append(String.valueOf(visitedUrl.getContentSize())).append(",").append(String.valueOf(visitedUrl.getOutgoingLinks())).append(",").append(visitedUrl.getContentType()).append("\n");
        }
        writer.close();

        file = new File("urls_latimes.csv");
        file.delete();
        file.createNewFile();
        writer = new BufferedWriter(new java.io.FileWriter(file, true));
        writer.append("URL, Residence\n");

        for (DiscoveredUrl discoveredUrl : resultStat.getDiscoveredUrls()) {
            writer.append(discoveredUrl.getUrl()).append(",").append(discoveredUrl.getResidence()).append("\n");
        }
        writer.close();
    }

    public static void getStat() throws IOException {
        int numFetchAttempted = resultStat.getNumFetchAttempted();
        Map<Integer, Integer> statusCodes = new HashMap<>();
        Map<Integer, String> statusDescription = new HashMap<>();

        int numFetchSucceeded = 0;

        for (FetchUrl fetchUrl : resultStat.getFetchUrls()) {
            int statusCode = fetchUrl.getStatusCode();
            if (statusCodes.containsKey(statusCode)) {
                statusCodes.put(statusCode, statusCodes.get(statusCode) + 1);
            } else {
                statusCodes.put(statusCode, 1);
                statusDescription.put(statusCode, fetchUrl.getStatusDescription());
            }
            if(statusCode >= 200 && statusCode < 300){
                numFetchSucceeded++;
            }
        }

        int numFetchAborted = numFetchAttempted - numFetchSucceeded;


        int numUniqueUrlsWithinResidence = 0;

        HashSet<String> uniqueDiscoveredUrls = new HashSet<>();

        for (DiscoveredUrl discoveredUrl : resultStat.getDiscoveredUrls()) {
            if(!uniqueDiscoveredUrls.contains(discoveredUrl.getUrl())){
                uniqueDiscoveredUrls.add(discoveredUrl.getUrl());
                if (discoveredUrl.getResidence().equals("OK")) {
                    numUniqueUrlsWithinResidence++;
                }
            }
        }

        int numUniqueUrls = uniqueDiscoveredUrls.size();
        int numUniqueUrlsOutsideResidence = numUniqueUrls - numUniqueUrlsWithinResidence;

        StringBuilder status_sb = new StringBuilder();

        statusCodes.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(
                entry -> status_sb.append(entry.getKey()).append(" ").append(statusDescription.get(entry.getKey())).append(": ").append(entry.getValue()).append("\n")
        );

        int oneK = 0, tenK = 0, hundredK = 0, oneM = 0, other = 0;
        Map<String, Integer> contentTypes = new HashMap<>();

        int numDiscoveredUrls = 0;

        for (VisitedUrl visitedUrl : resultStat.getVisitedUrls()) {
            int size = visitedUrl.getContentSize();
            if(size < 1024){
                oneK++;
            } else if(size < 10240){
                tenK++;
            } else if(size < 102400){
                hundredK++;
            } else if(size < 1048576){
                oneM++;
            } else {
                other++;
            }
            if (contentTypes.containsKey(visitedUrl.getContentType())) {
                contentTypes.put(visitedUrl.getContentType(), contentTypes.get(visitedUrl.getContentType()) + 1);
            } else {
                contentTypes.put(visitedUrl.getContentType(), 1);
            }
            numDiscoveredUrls += visitedUrl.getOutgoingLinks();
        }

        StringBuilder content_sb = new StringBuilder();
        statusCodes.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(
                entry -> content_sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n")
        );


        File file = new File("CrawlReport_latimes.txt");
        file.delete();
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file, true));
        writer.write("Name: Yufei Guo\n" +
                "USC ID: 5357788790\n"+
                "News site crawled: latimes.com\n" +
                "Number of threads: "+ numThread + "\n"
                +"\n"+
                "Fetch Statistics\n" +
                "================\n" +
                "# fetches attempted: " + numFetchAttempted + "\n" +
                "# fetches succeeded: " + numFetchSucceeded + "\n" +
                "# fetches aborted: " + numFetchAborted + "\n" +
                "\n"
        );
        writer.write(
                "Outgoing URLs:\n" +
                "==============\n" +
                "Total URLs extracted:\n" + numDiscoveredUrls + "\n" +
                "# unique URLs extracted: " + numUniqueUrls + "\n" +
                "# unique URLs within News Site: " + numUniqueUrlsWithinResidence + "\n" +
                "# unique URLs outside News Site: " + numUniqueUrlsOutsideResidence + "\n" +
                "\n"
        );
        writer.write(
                "Status Codes:\n" +
                "=============\n" +
                status_sb.toString() +
                "\n"
        );
        writer.write(
                "File Sizes:\n" +
                "===========\n" +
                "< 1KB: " + oneK + "\n" +
                "1KB ~ <10KB: " + tenK + "\n" +
                "10KB ~ <100KB: " + hundredK + "\n" +
                "100KB ~ <1MB: " + oneM + "\n" +
                ">= 1MB: " + other + "\n" +
                "\n"
        );
        writer.write(
                "Content Types:\n" +
                "==============\n" +
                content_sb.toString() +
                "\n"
        );
        writer.close();

    }

}// Main