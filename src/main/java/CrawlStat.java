import java.util.ArrayList;
import java.util.List;

class FetchUrl {
    public String url;
    public int statusCode;
    public String statusDescription;

    public FetchUrl(String url, int statusCode, String statusDescription) {
        this.url = url;
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
    }

    public String getUrl() {
        return url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }
}

class VisitedUrl{
    public String url;
    public int contentSize;
    public int outgoingLinks;
    public String contentType;

    public VisitedUrl(String url, int contentSize, int outgoingLinks, String contentType) {
        this.url = url;
        this.contentSize = contentSize;
        this.outgoingLinks = outgoingLinks;
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public int getContentSize() {
        return contentSize;
    }

    public int getOutgoingLinks() {
        return outgoingLinks;
    }

    public String getContentType() {
        return contentType;
    }
}

class DiscoveredUrl{
    public String url;
    public String residence; //0: not OK, 1: OK

    public DiscoveredUrl(String url, String reside) {
        this.url = url;
        this.residence = reside;
    }

    public String getUrl() {
        return url;
    }

    public String getResidence() {
        return residence;
    }
}


public class CrawlStat {

    // init arraylist for fetch, visit, and discovered urls
    public List<FetchUrl> fetchUrls;
    public List<VisitedUrl> visitedUrls;
    public List<DiscoveredUrl> discoveredUrls;

    public CrawlStat() {
        fetchUrls = new ArrayList<>();
        visitedUrls = new ArrayList<>();
        discoveredUrls = new ArrayList<>();
    }

    public void addFetchUrl(String url, int statusCode, String statusDescription) {
        fetchUrls.add(new FetchUrl(url, statusCode, statusDescription));
    }

    public void addVisitedUrl(String url, int contentSize, int outgoingLinks, String contentType) {
        visitedUrls.add(new VisitedUrl(url, contentSize, outgoingLinks, contentType));
    }

    public void addDiscoveredUrl(String url, String reside) {
        discoveredUrls.add(new DiscoveredUrl(url, reside));
    }

    public void addAllFetchUrls(List<FetchUrl> fetchUrls) {
        this.fetchUrls.addAll(fetchUrls);
    }

    public void addAllVisitedUrls(List<VisitedUrl> visitedUrls) {
        this.visitedUrls.addAll(visitedUrls);
    }

    public void addAllDiscoveredUrls(List<DiscoveredUrl> discoveredUrls) {
        this.discoveredUrls.addAll(discoveredUrls);
    }

    public List<FetchUrl> getFetchUrls() {
        return fetchUrls;
    }

    public List<VisitedUrl> getVisitedUrls() {
        return visitedUrls;
    }

    public List<DiscoveredUrl> getDiscoveredUrls() {
        return discoveredUrls;
    }

    public int getNumFetchAttempted(){
        return fetchUrls.size();
    }


    public int getNumDiscoveredUrls() {
        return discoveredUrls.size();
    }
}
