package scraper.domain.santander.session;

import scraper.domain.AccountDetails;
import scraper.domain.InvalidCredentialsException;
import scraper.domain.http.Response;
import scraper.domain.santander.Credentials;

import java.util.Date;
import java.util.List;

import static scraper.domain.santander.session.HttpResponseParser.*;

public class Session {

  private final HttpExchanges exchanges;

  public Session(HttpExchanges exchanges) {
    this.exchanges = exchanges;
  }

  public FirstLayerAuthenticator initAuthenticator(Credentials credentials) {
    return new FirstLayerAuthenticator(credentials);
  }

  public SecondLayerAuthenticator firstAuthenticationFactor(FirstLayerAuthenticator authenticator) {
    return authenticator.authenticate();
  }

  public AccountsImporter secondAuthenticationFactor(SecondLayerAuthenticator authenticator, String smsCode) {
    return authenticator.authenticate(smsCode);
  }

  public List<AccountDetails> importAccounts(AccountsImporter importer) {
    return importer.importAccounts();
  }

  public class FirstLayerAuthenticator {

    private final Credentials credentials;

    private FirstLayerAuthenticator(Credentials credentials) {
      this.credentials = credentials;
    }

    private SecondLayerAuthenticator authenticate() {
      String redirectXmlPath = extractXmlPathFromLoginPage(exchanges.loginPage().body);
      String nikPagePath = extractPathFromRedirectRequest(redirectXmlPath);
      String passPagePath = extractPasswordPagePathFromNikResponse(exchanges.nik(nikPagePath, credentials.accountNumber()).body);
      String passwordPath = extractPasswordPathFromPasswordPage(exchanges.passwordPage(passPagePath).body);
      String smsCodeConfirmationPath = extractSmsCodePathFromPasswordResponse(exchanges.password(passwordPath, credentials.password()).body);
      return new SecondLayerAuthenticator(smsCodeConfirmationPath);
    }

    private String extractPathFromRedirectRequest(String basePath) {
      long timestamp = new Date().getTime();
      String fullPathForXml = basePath + "&_=" + timestamp;
      Response response = exchanges.redirectXml(fullPathForXml);
      return extractNikPagePathFromRedirectXml(response.body);
    }

  }

  public class SecondLayerAuthenticator {

    private final String smsCodeConfirmationPath;

    private SecondLayerAuthenticator(String smsCodeConfirmationPath) {
      this.smsCodeConfirmationPath = smsCodeConfirmationPath;
    }

    private AccountsImporter authenticate(String smsCode) {
      Response response = exchanges.smsCode(smsCodeConfirmationPath, smsCode);
      if (!hasLogoutButton(response.body))
        throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
      String productsPath = extractProductsPathFromDashboardPage(response.body);
      return new AccountsImporter(productsPath);
    }

  }

  public class AccountsImporter {

    private final String productsPath;

    private AccountsImporter(String productsPath) {
      this.productsPath = productsPath;
    }

    private List<AccountDetails> importAccounts() {
      Response response = exchanges.productsPage(productsPath);
      List<AccountDetails> accountsDetails = extractAccountsInformationFromProductsPage(response.body);
      exchanges.logout();
      return accountsDetails;
    }

  }

}