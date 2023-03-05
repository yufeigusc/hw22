import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.regex.Pattern;
import java.util.Set;


// -need- to implement visit() and shouldVisit()
public class MyCrawler extends WebCrawler {

    static String regex = ".*\\.(css|js|bmp|ico|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|ram|m4v|mkv|ogg|ogv|ps|eps|tex|ppt|pptx|xls|xlsx|names|data|dat|exe|bz2|tar|msi|bin|7z|psd|dmg|iso|epub|dll|cnf|tgz|sha1|thmx|mso|arff|rtf|jar|csv|rm|smil|wmv|swf|wma|zip|rar|gz)$";
    private static final Pattern FILTER = Pattern.compile(regex);

    public CrawlStat crawlStat;

    //private final AtomicInteger numSeenImages;

    /**
     * Creates a new crawler instance.
     *
//     * @param numSeenImages This is just an example to demonstrate how you can pass objects to crawlers. In this
     * example, we pass an AtomicInteger to all crawlers and they increment it whenever they see a url which points
     * to an image.
     */
    public MyCrawler() {
        //this.numSeenImages = 10;
        crawlStat = new CrawlStat();
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */

    public String norm_url(String url){
        String result = "";
        url = url.toLowerCase();
        if(url.endsWith("/"))
            result = url.substring(0, url.length()-1);
        // decode url
        try {
            result = java.net.URLDecoder.decode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = norm_url(url.getURL());
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (FILTER.matcher(href).matches()) {
            //numSeenImages.incrementAndGet();
            return false;
        }

        if(href.startsWith("https://www.latimes.com/")|| href.startsWith("https://latimes.com/"))
            crawlStat.addDiscoveredUrl(href, "OK");
        else
            crawlStat.addDiscoveredUrl(href, "N_OK");

        // only do pages in our course site
        return href.startsWith("https://www.latimes.com");
    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        crawlStat.addFetchUrl(webUrl.getURL(), statusCode,  statusDescription);
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by our program.
     */
    @Override
    public void visit(Page page) {
//        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
//        String domain = page.getWebURL().getDomain();
//        String path = page.getWebURL().getPath();
//        String subDomain = page.getWebURL().getSubDomain();
//        String parentUrl = page.getWebURL().getParentUrl();
//        String anchor = page.getWebURL().getAnchor();
        String contentType = page.getContentType().split(";")[0];

//        System.out.println("Docid: " + docid);
//        System.out.println("URL: " + url);
//        System.out.println("Domain: " +  domain);
//        System.out.println("Sub-domain: " + subDomain);
//        System.out.println("Path: "+ path);
//        System.out.println("Parent page: " + parentUrl);
//        System.out.println("Anchor text: " +  anchor);

        if(contentType.equals("text/html")){
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            crawlStat.addVisitedUrl(url, page.getContentData().length, links.size(), contentType);
        }
        // if content type is "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "image/jpeg", or "image/png"
        else if (contentType.equals("application/msword") || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/svg+xml")
                || contentType.equals("application/pdf")) {
            crawlStat.addVisitedUrl(url, page.getContentData().length, 0, contentType);
        }
        // content type list : https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types

//        if (page.getParseData() instanceof HtmlParseData) {
//            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//            String text = htmlParseData.getText();
//            String html = htmlParseData.getHtml();
//            Set<WebURL> links = htmlParseData.getOutgoingUrls();
//
//            /*logger.debug("Text length: {}", text.length());
//            logger.debug("Html length: {}", html.length());
//            logger.debug("Number of outgoing links: {}", links.size());*/
//        }

//        Header[] responseHeaders = page.getFetchResponseHeaders();
//        if (responseHeaders != null) {
//            System.out.println("Response headers:");
//            for (Header header : responseHeaders) {
//                //logger.debug("\t{}: {}", header.getName(), header.getValue());
//                System.out.println(header.getName() + "," + header.getValue());
//            }
//        }
//
//        System.out.println("\n==========================\n");
    }

    @Override
    public Object getMyLocalData() {
        return crawlStat;
    }

}// BasicCrawler