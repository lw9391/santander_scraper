package scraper.santander.actions;

import org.jsoup.nodes.Document;
import scraper.santander.InvalidCredentialsException;

import static scraper.santander.actions.HttpResponseParser.extractProductsPathFromDashboardPage;
import static scraper.santander.actions.HttpResponseParser.hasLogoutButton;

public class SubmitSmsCode {

  private final SantanderHttpApi exchanges;
  private final String smsCodeConfirmationPath;

  SubmitSmsCode(SantanderHttpApi session, String smsCodeConfirmationPath) {
    exchanges = session;
    this.smsCodeConfirmationPath = smsCodeConfirmationPath;
  }

  public ImportAccounts run(String smsCode) {
    Document response = exchanges.smsCode(smsCodeConfirmationPath, smsCode);
    if (!hasLogoutButton(response))
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
    String productsPath = extractProductsPathFromDashboardPage(response);
    return new ImportAccounts(exchanges, productsPath);
  }

}
