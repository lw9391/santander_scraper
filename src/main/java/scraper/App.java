package scraper;

import scraper.domain.http.Fetcher;
import scraper.domain.http.okhttp.OkHttpFetcher;
import scraper.domain.santander.AccountsScraper;
import scraper.domain.santander.session.RequestHandler;
import scraper.domain.santander.session.HttpRequests;
import scraper.domain.santander.session.Session;

public class App {

  public static void main(String... args) {
    if (args.length != 2)
      throw new IllegalStateException("Wprowadź nik i hasło przez parametry wiersza poleceń.");

    AccountsScraper scraper = initScraper();
    scraper.run(args[0], args[1]);
  }

  private static AccountsScraper initScraper() {
    Fetcher sender = new OkHttpFetcher();
    HttpRequests provider = new HttpRequests("https://www.centrum24.pl/centrum24-web");
    RequestHandler requestHandler = new RequestHandler(sender, provider);
    Session session = new Session(requestHandler);
    return new AccountsScraper(session, new Console());
  }

}