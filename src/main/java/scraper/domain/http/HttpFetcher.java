package scraper.domain.http;

public interface HttpFetcher {

  Response send(Request request);

}