package scraper.santander.http;

public interface Fetcher {

  Response send(Request request);

}