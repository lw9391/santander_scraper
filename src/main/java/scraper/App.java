package scraper;

import scraper.santander.AccountsScraper;
import scraper.santander.actions.HttpExchanges;
import scraper.santander.http.okhttp.OkHttpFetcher;

public class App {

  public static void main(String... args) {
    if (args.length != 2)
      throw new IllegalStateException("Wprowadź nik i hasło przez parametry wiersza poleceń.");

    AccountsScraper scraper = initScraper();
    scraper.run(args[0], args[1]);
  }

  private static AccountsScraper initScraper() {
    var fetcher = new OkHttpFetcher();
    var exchanges = new HttpExchanges("https://www.centrum24.pl/centrum24-web", fetcher);
    return new AccountsScraper(exchanges, new Console());
  }

}