package scraper;

import scraper.connections.HttpRequestSender;
import scraper.connections.okhttp.OkHttpRequestsSender;
import scraper.santander.SantanderAccountsScraper;
import scraper.santander.session.RequestHandler;
import scraper.santander.session.SantanderRequestProvider;
import scraper.santander.session.SantanderSession;
import scraper.view.ConsoleController;

public class App {

  public static void main(String... args) {
    if (args.length != 2) {
      throw new IllegalStateException("Wprowadź nik i hasło przez parametry wiersza poleceń.");
    }
    Credentials credentials = new Credentials(args[0], args[1]);
    SantanderAccountsScraper scraper = initScraper();
    scraper.run(credentials);
  }

  private static SantanderAccountsScraper initScraper() {
    HttpRequestSender sender = new OkHttpRequestsSender();
    SantanderRequestProvider provider = new SantanderRequestProvider("https://www.centrum24.pl");
    RequestHandler requestHandler = new RequestHandler(sender, provider);
    SantanderSession session = new SantanderSession(requestHandler);
    return new SantanderAccountsScraper(session, new ConsoleController());
  }
}