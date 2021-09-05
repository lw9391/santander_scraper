package scraper;

import scraper.session.InvalidCredentialsException;
import scraper.session.SantanderSession;

import java.io.IOException;
import java.util.List;

public class SantanderAccountsScraper implements Logable, LoginCodeConfirmable, AccountsInfoScraper {
    public static final String HOST = "https://www.centrum24.pl";
    public static final String PATH = "/centrum24-web";
    public static final String DASHBOARD_PATH = "/centrum24-web/multi";
    public static final String LOGOUT = "https://www.centrum24.pl/centrum24-web/logout";

    private final SantanderSession session;

    public SantanderAccountsScraper(SantanderSession session) {
        this.session = session;
    }

    @Override
    public boolean logIn(Credentials credentials) {
        try {
            session.sendNikRequest(credentials.getAccountNumber());
            session.sendPasswordRequest(credentials.getPassword());
            return true;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logOut() {
        try {
            session.logOut();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean confirmAccess(String token) {
        try {
            session.sendTokenRequest(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidCredentialsException ice) {
            System.out.println("Wprowadzono złe dane logowania - nik, hasło, sms-kod lub kilka z nich.");
        }
        return true;
    }

    @Override
    public List<AccountDetails> scrapAccountsInfo() {
        try {
            return session.sendAccountsDetailsRequest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
