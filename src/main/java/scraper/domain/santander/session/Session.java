package scraper.domain.santander.session;

import scraper.domain.AccountDetails;
import scraper.domain.santander.Credentials;

import java.util.List;

public class Session {

  private final RequestHandler requestHandler;

  public Session(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
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
      String redirectXmlPath = requestHandler.sendLoginPageRequest();
      String nikPagePath = requestHandler.sendRedirectXmlRequest(redirectXmlPath);
      String passPagePath = requestHandler.sendNikRequest(nikPagePath, credentials.accountNumber());
      String passwordPath = requestHandler.sendPasswordPageRequest(passPagePath);
      String smsCodeConfirmationPath = requestHandler.sendPasswordRequest(passwordPath, credentials.password());
      return new SecondLayerAuthenticator(smsCodeConfirmationPath);
    }

  }

  public class SecondLayerAuthenticator {

    private final String smsCodeConfirmationPath;

    private SecondLayerAuthenticator(String smsCodeConfirmationPath) {
      this.smsCodeConfirmationPath = smsCodeConfirmationPath;
    }

    private AccountsImporter authenticate(String smsCode) {
      String productsPath = requestHandler.sendSmsCodeRequest(smsCodeConfirmationPath, smsCode);
      return new AccountsImporter(productsPath);
    }

  }

  public class AccountsImporter {

    private final String productsPath;

    private AccountsImporter(String productsPath) {
      this.productsPath = productsPath;
    }

    private List<AccountDetails> importAccounts() {
      List<AccountDetails> accountsDetails = requestHandler.scrapeAccountsInformation(productsPath);
      requestHandler.sendLogoutRequest();
      return accountsDetails;
    }

  }

}