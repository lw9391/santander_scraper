package scraper;

import scraper.domain.connections.HttpRequestSender;
import scraper.domain.connections.okhttp.OkHttpRequestsSender;
import scraper.domain.santander.SantanderAccountsScraper;
import scraper.domain.santander.session.RequestHandler;
import scraper.domain.santander.session.SantanderHttpRequestsProvider;
import scraper.domain.santander.session.SantanderSession;

public class App {

  public static void main(String... args) {
    if (args.length != 2)
      throw new IllegalStateException("Wprowadź nik i hasło przez parametry wiersza poleceń.");

    SantanderAccountsScraper scraper = initScraper();
    scraper.run(args[0], args[1]);
  }

  private static SantanderAccountsScraper initScraper() {
    HttpRequestSender sender = new OkHttpRequestsSender();
    SantanderHttpRequestsProvider provider = new SantanderHttpRequestsProvider("https://www.centrum24.pl");
    RequestHandler requestHandler = new RequestHandler(sender, provider);
    SantanderSession session = new SantanderSession(requestHandler);
    return new SantanderAccountsScraper(session, new ConsoleController());
  }

}