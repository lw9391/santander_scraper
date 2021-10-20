package scraper.domain.http;

public interface HttpFetcher {

  Response sendGET(Request request);

  Response sendPOST(Request request);

}