package scraper;

import scraper.domain.http.Fetcher;
import scraper.domain.http.okhttp.OkHttpFetcher;
import scraper.domain.santander.AccountsScraper;
import scraper.domain.santander.session.HttpExchanges;
import scraper.domain.santander.session.Session;

public class App {

  public static void main(String... args) {
    if (args.length != 2)
      throw new IllegalStateException("Wprowadź nik i hasło przez parametry wiersza poleceń.");

    AccountsScraper scraper = initScraper();
    scraper.run(args[0], args[1]);
  }

  private static AccountsScraper initScraper() {
    Fetcher fetcher = new OkHttpFetcher();
    HttpExchanges exchanges = new HttpExchanges("https://www.centrum24.pl/centrum24-web", fetcher);
    Session session = new Session(exchanges);
    return new AccountsScraper(session, new Console());
  }

}