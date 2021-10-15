package scraper.santander.session;

import scraper.AccountDetails;
import scraper.santander.Credentials;

import java.util.List;

public class SantanderSession {

  private final RequestHandler requestHandler;

  public SantanderSession(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  public FirstAuthFactorToken firstAuthorizationFactor(Credentials credentials) {
    String redirectXmlPath = requestHandler.sendLoginPageRequest();
    String nikPagePath = requestHandler.sendRedirectXmlRequest(redirectXmlPath);
    String passPagePath = requestHandler.sendNikRequest(nikPagePath, credentials.accountNumber);
    String passwordPath = requestHandler.sendPasswordPageRequest(passPagePath);
    String smsCodeConfirmationPath = requestHandler.sendPasswordRequest(passwordPath, credentials.password);
    return new FirstAuthFactorToken(smsCodeConfirmationPath);
  }

  public SecondAuthFactorToken secondAuthorizationFactor(FirstAuthFactorToken token, String smsCode) {
    String productsPath = requestHandler.sendSmsCodeRequest(token.smsCodeConfirmationPath, smsCode);
    return new SecondAuthFactorToken(productsPath);
  }

  public List<AccountDetails> scrapeAccountsDetails(SecondAuthFactorToken token) {
    List<AccountDetails> accountsDetails = requestHandler.scrapeAccountsInformation(token.productsPath);
    requestHandler.sendLogoutRequest();
    return accountsDetails;
  }

  public static class FirstAuthFactorToken {

    private final String smsCodeConfirmationPath;

    private FirstAuthFactorToken(String smsCodeConfirmationPath) {
      this.smsCodeConfirmationPath = smsCodeConfirmationPath;
    }

  }

  public static class SecondAuthFactorToken {

    private final String productsPath;

    private SecondAuthFactorToken(String productsPath) {
      this.productsPath = productsPath;
    }

  }

}