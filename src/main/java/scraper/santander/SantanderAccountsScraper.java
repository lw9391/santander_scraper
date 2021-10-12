package scraper.santander;

import scraper.AccountDetails;
import scraper.Credentials;
import scraper.CredentialsVerifier;
import scraper.santander.session.SantanderSession;
import scraper.view.ViewController;
import scraper.InvalidCredentialsException;

import java.util.List;

public class SantanderAccountsScraper {
  public static final String PROMPT_FOR_SMS_CODE = "Wprowadz sms-kod:";
  private final SantanderSession session;
  private final CredentialsVerifier credentialsVerifier;
  private final ViewController viewController;

  public SantanderAccountsScraper(SantanderSession session, ViewController viewController) {
    this.session = session;
    this.viewController = viewController;
    this.credentialsVerifier = new SantanderCredentialsVerifier();
  }

  public void run(Credentials credentials) {
    logIn(credentials);
    scrapeAccountsInfo();
    session.logOut();
  }

  private void logIn(Credentials credentials) {
    verifyCredentials(credentials);
    session.sendNikRequest(credentials.getAccountNumber());
    session.sendPasswordRequest(credentials.getPassword());

    viewController.displayMessage(PROMPT_FOR_SMS_CODE);
    String token = viewController.readInput();
    verifyToken(token);
    session.sendTokenRequest(token);
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

  private void scrapeAccountsInfo() {
    List<AccountDetails> accountDetails = session.sendAccountsDetailsRequest();
    viewController.displayOutput(accountDetails);
  }
}
