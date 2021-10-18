package scraper.domain.santander;

import scraper.domain.AccountDetails;
import scraper.domain.santander.session.SantanderSession;
import scraper.ViewController;
import scraper.domain.InvalidCredentialsException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static scraper.domain.santander.session.SantanderSession.FirstAuthFactorToken;
import static scraper.domain.santander.session.SantanderSession.SecondAuthFactorToken;

public class SantanderAccountsScraper {

  private final SantanderSession session;
  private final ViewController viewController;

  public SantanderAccountsScraper(SantanderSession session, ViewController viewController) {
    this.session = session;
    this.viewController = viewController;
  }

  public void run(String nik, String password) {
    Credentials credentials = new Credentials(nik, password);
    FirstAuthFactorToken firstAuthFactorToken = session.firstAuthorizationFactor(credentials);
    SecondAuthFactorToken secondAuthFactorToken = session.secondAuthorizationFactor(firstAuthFactorToken, readSmsCode());
    List<AccountDetails> accountDetails = session.scrapeAccountsDetails(secondAuthFactorToken);
    viewController.displayOutput(accountDetails);
  }

  private String readSmsCode() {
    viewController.displayPromptForSmsCode();
    String smsCode = viewController.readInput();
    assertTokenHasValidFormat(smsCode);
    return smsCode;
  }

  private static void assertTokenHasValidFormat(String token) {
    String regex = "[0-9]{3}\\-[0-9]{3}";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(token);
    if (!matcher.matches())
      throw new InvalidCredentialsException("Provided token has invalid format.");
  }

}