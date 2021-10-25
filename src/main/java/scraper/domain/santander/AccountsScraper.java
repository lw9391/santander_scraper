package scraper.domain.santander;

import scraper.domain.AccountDetails;
import scraper.domain.InvalidCredentialsException;
import scraper.domain.View;
import scraper.domain.santander.session.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountsScraper {

  private final HttpExchanges exchanges;
  private final View view;

  public AccountsScraper(HttpExchanges exchanges, View view) {
    this.exchanges = exchanges;
    this.view = view;
  }

  public void run(String nik, String password) {
    Credentials credentials = new Credentials(nik, password);
    FirstLayerAuthenticator authenticator = new FirstLayerAuthenticator(exchanges, credentials);
    SecondLayerAuthenticator secondLayerAuthenticator = authenticator.authenticate();
    AccountsImporter accountsImporter = secondLayerAuthenticator.authenticate(readSmsCode());
    List<AccountDetails> accountsDetails = accountsImporter.importAccounts();
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
