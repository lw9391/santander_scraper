package scraper;

import scraper.session.InvalidCredentialsException;
import scraper.session.SantanderSession;

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
        session.sendNikRequest(credentials.getAccountNumber());
        session.sendPasswordRequest(credentials.getPassword());
        return true;
    }

    private void verifyCredentials(Credentials credentials) {
        if (!credentialsVerifier.verifyAccountNumber(credentials.getAccountNumber())) {
            throw new InvalidCredentialsException("Nik must be between 6 and 20 characters.");
        }
        if (!credentialsVerifier.verifyPassword(credentials.getPassword())) {
            throw new InvalidCredentialsException("Password must be between 4 and 20 characters.");
        }
    }

    @Override
    public void logOut() {
        session.logOut();
    }

    @Override
    public boolean confirmAccess(String token) {
        verifyToken(token);
        session.sendTokenRequest(token);
        return true;
    }

    private void verifyToken(String token) {
        if (!credentialsVerifier.verifyToken(token)) {
            throw new InvalidCredentialsException("Provided token has invalid format.");
        }
    }

    @Override
    public List<AccountDetails> scrapeAccountsInfo() {
        return session.sendAccountsDetailsRequest();
    }
}
