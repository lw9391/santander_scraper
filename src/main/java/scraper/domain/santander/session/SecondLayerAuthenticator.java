package scraper.domain.santander.session;

import scraper.domain.InvalidCredentialsException;
import scraper.domain.http.Response;

import static scraper.domain.santander.session.HttpResponseParser.extractProductsPathFromDashboardPage;
import static scraper.domain.santander.session.HttpResponseParser.hasLogoutButton;

public class SecondLayerAuthenticator {

  private final HttpExchanges exchanges;
  private final String smsCodeConfirmationPath;

  SecondLayerAuthenticator(HttpExchanges session, String smsCodeConfirmationPath) {
    this.exchanges = session;
    this.smsCodeConfirmationPath = smsCodeConfirmationPath;
  }

  public AccountsImporter authenticate(String smsCode) {
    Response response = exchanges.smsCode(smsCodeConfirmationPath, smsCode);
    if (!hasLogoutButton(response.body))
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
    String productsPath = extractProductsPathFromDashboardPage(response.body);
    return new AccountsImporter(exchanges, productsPath);
  }

}
