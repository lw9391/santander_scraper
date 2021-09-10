package scraper;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import scraper.connections.HttpRequestSender;
import scraper.connections.okhttp.OkHttpRequestsSender;
import scraper.santander.SantanderAccountsScraper;
import scraper.santander.session.RequestHandler;
import scraper.santander.session.SantanderRequestProvider;
import scraper.santander.session.SantanderSession;
import scraper.view.ConsoleController;
import scraper.view.ViewController;

import java.net.CookieManager;
import java.net.CookiePolicy;

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
        HttpRequestSender sender = initRequestSender();
        SantanderRequestProvider provider = new SantanderRequestProvider(host);
        RequestHandler requestHandler = new RequestHandler(sender, provider);
        SantanderSession session = new SantanderSession(requestHandler);
        if (controller == null) {
            controller = new ConsoleController();
        }
        return new SantanderAccountsScraper(session, controller);
    }

    private static HttpRequestSender initRequestSender() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.cache(null)
                .cookieJar(cookieJar)
                .build();

        return new OkHttpRequestsSender(client);
    }

    public static void setViewController(ViewController viewController) {
        controller = viewController;
    }

    public static void setLocalhost(boolean useLocal) {
        useLocalhost = useLocal;
    }
}