import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import scraper.Credentials;
import scraper.SantanderAccountsScraper;
import scraper.session.RequestHandler;
import scraper.session.SantanderSession;
import scraper.session.connections.ConnectionHandler;
import scraper.session.connections.OkHttpConnectionHandler;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Scanner;

public class App {
    private final SantanderAccountsScraper scraper;

    public App() {
        ConnectionHandler connectionHandler = initConnectionHandler();
        RequestHandler requestHandler = new RequestHandler(connectionHandler);
        SantanderSession session = new SantanderSession(requestHandler);
        scraper = new SantanderAccountsScraper(session);
    }

    private ConnectionHandler initConnectionHandler() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.cache(null)
                .cookieJar(cookieJar)
                .build();

        return new OkHttpConnectionHandler(client);
    }

    public void printAccountsInformation(Credentials credentials) {
        scraper.logIn(credentials);
        scraper.confirmAccess(readTokenFromCmd());
        scraper.scrapAccountsInfo().forEach(System.out::println);
        scraper.logOut();
    }

    private String readTokenFromCmd() {
        System.out.println("Wprowadź sms-kod:");
        Scanner in = new Scanner((System.in));
        String input = in.nextLine();
        in.close();
        return input;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalStateException("Wprowadź nik i hasło przez parametry wiersza poleceń.");
        }
        Credentials credentials = new Credentials(args[0], args[1]);
        App app = new App();
        app.printAccountsInformation(credentials);
    }
}