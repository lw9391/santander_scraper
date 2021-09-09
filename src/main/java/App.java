import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import scraper.ConsoleController;
import scraper.Credentials;
import scraper.SantanderAccountsScraper;
import scraper.session.RequestHandler;
import scraper.session.SantanderSession;
import scraper.session.connections.OkHttpRequestsSender;
import scraper.session.connections.SantanderConnectionHandler;

import java.net.CookieManager;
import java.net.CookiePolicy;

public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalStateException("Wprowadź nik i hasło przez parametry wiersza poleceń.");
        }
        Credentials credentials = new Credentials(args[0], args[1]);
        SantanderAccountsScraper scraper = initScraper();
        scraper.run(credentials);
    }

    private static SantanderAccountsScraper initScraper() {
        SantanderConnectionHandler connectionHandler = initConnectionHandler();
        RequestHandler requestHandler = new RequestHandler(connectionHandler);
        SantanderSession session = new SantanderSession(requestHandler);
        return new SantanderAccountsScraper(session, new ConsoleController());
    }

    private static SantanderConnectionHandler initConnectionHandler() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.cache(null)
                .cookieJar(cookieJar)
                .build();

        OkHttpRequestsSender okHttpRequestsSender = new OkHttpRequestsSender(client);
        return new SantanderConnectionHandler(okHttpRequestsSender);
    }
}