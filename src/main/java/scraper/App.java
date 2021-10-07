package scraper;

import scraper.connections.HttpRequestSender;
import scraper.connections.okhttp.OkHttpRequestsSender;
import scraper.santander.SantanderAccountsScraper;
import scraper.santander.session.RequestHandler;
import scraper.santander.session.SantanderRequestProvider;
import scraper.santander.session.SantanderSession;
import scraper.view.ConsoleController;
import scraper.view.ViewController;

public class App {
    private static ViewController controller;
    private static boolean useLocalhost = false;

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalStateException("Wprowadź nik i hasło przez parametry wiersza poleceń.");
        }
        Credentials credentials = new Credentials(args[0], args[1]);
        String host = "https://www.centrum24.pl";
        if (useLocalhost) {
            host = "http://127.0.0.1:8889";
        }

        SantanderAccountsScraper scraper = initScraper(host);
        scraper.run(credentials);
    }

    private static SantanderAccountsScraper initScraper(String host) {
        HttpRequestSender sender = new OkHttpRequestsSender();
        SantanderRequestProvider provider = new SantanderRequestProvider(host);
        RequestHandler requestHandler = new RequestHandler(sender, provider);
        SantanderSession session = new SantanderSession(requestHandler);
        if (controller == null) {
            controller = new ConsoleController();
        }
        return new SantanderAccountsScraper(session, controller);
    }

    public static void setViewController(ViewController viewController) {
        controller = viewController;
    }

    public static void setLocalhost(boolean useLocal) {
        useLocalhost = useLocal;
    }
}