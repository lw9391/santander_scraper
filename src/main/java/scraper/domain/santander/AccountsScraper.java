package scraper.domain.santander;

import scraper.domain.AccountDetails;
import scraper.domain.InvalidCredentialsException;
import scraper.domain.View;
import scraper.domain.santander.session.Session;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountsScraper {

  private final Session session;
  private final View view;

  public AccountsScraper(Session session, View view) {
    this.session = session;
    this.view = view;
  }

  public void run(String nik, String password) {
    Credentials credentials = new Credentials(nik, password);
    Session.FirstLayerAuthenticator authenticator = session.initAuthenticator(credentials);
    Session.SecondLayerAuthenticator secondLayerAuthenticator = session.firstAuthenticationFactor(authenticator);
    Session.AccountsImporter accountsImporter = session.secondAuthenticationFactor(secondLayerAuthenticator, readSmsCode());
    List<AccountDetails> accountsDetails = session.importAccounts(accountsImporter);
    view.display(accountsDetails);
  }

  private String readSmsCode() {
    String smsCode = view.readSmsCode();
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
