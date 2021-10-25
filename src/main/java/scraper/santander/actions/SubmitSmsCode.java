package scraper.santander.actions;

import scraper.santander.InvalidCredentialsException;
import scraper.santander.http.Response;

import static scraper.santander.actions.HttpResponseParser.extractProductsPathFromDashboardPage;
import static scraper.santander.actions.HttpResponseParser.hasLogoutButton;

public class SubmitSmsCode {

  private final HttpExchanges exchanges;
  private final String smsCodeConfirmationPath;

  SubmitSmsCode(HttpExchanges session, String smsCodeConfirmationPath) {
    exchanges = session;
    this.smsCodeConfirmationPath = smsCodeConfirmationPath;
  }

  public ImportAccounts run(String smsCode) {
    Response response = exchanges.smsCode(smsCodeConfirmationPath, smsCode);
    if (!hasLogoutButton(response.body))
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
    String productsPath = extractProductsPathFromDashboardPage(response.body);
    return new ImportAccounts(exchanges, productsPath);
  }

}
