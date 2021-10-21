package scraper.domain.http;

public interface Fetcher {

  Response send(Request request);

}