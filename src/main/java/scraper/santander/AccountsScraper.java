package scraper.santander;

import scraper.santander.actions.ImportAccounts;
import scraper.santander.actions.SantanderHttpApi;
import scraper.santander.actions.SubmitLoginAndPassword;
import scraper.santander.actions.SubmitSmsCode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountsScraper {

  private final SantanderHttpApi exchanges;
  private final View view;

  public AccountsScraper(SantanderHttpApi exchanges, View view) {
    this.exchanges = exchanges;
    this.view = view;
  }

  public void run(String nik, String password) {
    var credentials = new Credentials(nik, password);
    SubmitSmsCode secondStep = new SubmitLoginAndPassword(exchanges, credentials).run();
    ImportAccounts accountsImporter = secondStep.run(readSmsCode());
    List<Account> accountsDetails = accountsImporter.run();
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
