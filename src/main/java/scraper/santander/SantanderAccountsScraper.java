package scraper.santander;

import scraper.AccountDetails;
import scraper.santander.session.SantanderSession;
import scraper.view.ViewController;
import scraper.InvalidCredentialsException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SantanderAccountsScraper {
  public static final String PROMPT_FOR_SMS_CODE = "Wprowadz sms-kod:";
  private final SantanderSession session;
  private final ViewController viewController;

  public SantanderAccountsScraper(SantanderSession session, ViewController viewController) {
    this.session = session;
    this.viewController = viewController;
  }

  public void run(String nik, String password) {
    Credentials credentials = new Credentials(nik, password);
    logIn(credentials);
    scrapeAccountsInfo();
    session.logOut();
  }

  private void logIn(Credentials credentials) {
    session.sendNikRequest(credentials.accountNumber);
    session.sendPasswordRequest(credentials.password);

    viewController.displayMessage(PROMPT_FOR_SMS_CODE);
    String token = viewController.readInput();
    verifyToken(token);
    session.sendTokenRequest(token);
  }

  private void verifyToken(String token) {
    String regex = "[0-9]{3}\\-[0-9]{3}";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(token);
    if (!matcher.matches()) {
      throw new InvalidCredentialsException("Provided token has invalid format.");
    }
  }

  private void scrapeAccountsInfo() {
    List<AccountDetails> accountDetails = session.sendAccountsDetailsRequest();
    viewController.displayOutput(accountDetails);
  }
}
