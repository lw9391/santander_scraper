package scraper.santander;

import scraper.AccountDetails;
import scraper.AccountsInfoScraper;
import scraper.Credentials;
import scraper.CredentialsVerifier;
import scraper.Logable;
import scraper.santander.session.SantanderSession;
import scraper.view.ViewController;
import scraper.InvalidCredentialsException;

import java.util.List;

public class SantanderAccountsScraper implements Logable, AccountsInfoScraper {
    private final SantanderSession session;
    private final CredentialsVerifier credentialsVerifier;
    private final ViewController viewController;

    public SantanderAccountsScraper(SantanderSession session, ViewController viewController) {
        this.session = session;
        this.viewController = viewController;
        this.credentialsVerifier = new SantanderCredentialsVerifier();
    }

    @Override
    public boolean logIn(Credentials credentials) {
        verifyCredentials(credentials);
        session.sendNikRequest(credentials.getAccountNumber());
        session.sendPasswordRequest(credentials.getPassword());

        viewController.displayMessage("Wprowadz sms-kod:");
        String token = viewController.readInput();
        verifyToken(token);
        session.sendTokenRequest(token);
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

    private void verifyToken(String token) {
        if (!credentialsVerifier.verifyToken(token)) {
            throw new InvalidCredentialsException("Provided token has invalid format.");
        }
    }

    @Override
    public void logOut() {
        session.logOut();
    }

    @Override
    public void scrapeAccountsInfo() {
        List<AccountDetails> accountDetails = session.sendAccountsDetailsRequest();
        viewController.displayOutput(accountDetails);
    }

    public void run(Credentials credentials) {
        logIn(credentials);
        scrapeAccountsInfo();
        logOut();
    }
}
