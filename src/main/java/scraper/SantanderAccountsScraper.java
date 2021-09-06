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
    private final CredentialsVerifier credentialsVerifier;

    public SantanderAccountsScraper(SantanderSession session) {
        this.session = session;
        this.credentialsVerifier = new SantanderCredentialsVerifier();
    }

    @Override
    public boolean logIn(Credentials credentials) {
        verifyCredentials(credentials);
        try {
            session.sendNikRequest(credentials.getAccountNumber());
            session.sendPasswordRequest(credentials.getPassword());
            return true;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyCredentials(Credentials credentials) {
        if (!credentialsVerifier.verifyAccountNumber(credentials.getAccountNumber())) {
            throw new InvalidCredentialsException("Nik must be between 6 and 20 characters.");
        }
        if (!credentialsVerifier.verifyPassword(credentials.getPassword())) {
            throw new InvalidCredentialsException("Password must be between  and 20 characters.");
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
        verifyToken(token);
        try {
            session.sendTokenRequest(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void verifyToken(String token) {
        if (!credentialsVerifier.verifyToken(token)) {
            throw new InvalidCredentialsException("Provided token has invalid format.");
        }
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
