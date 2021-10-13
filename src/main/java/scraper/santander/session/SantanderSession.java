package scraper.santander.session;

import scraper.AccountDetails;
import scraper.santander.Credentials;
import scraper.santander.PathsNames;

import java.util.List;
import java.util.Map;

import static scraper.santander.PathsNames.*;

public class SantanderSession {
  private final RequestHandler requestHandler;

  public SantanderSession(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  public FirstAuthFactorToken firstAuthorizationFactor(Credentials credentials) {
    String pathForXml = requestHandler.sendLoginPageRequest();
    String pathForNikPage = requestHandler.sendRedirectXmlRequest(pathForXml);
    pauseExecution();
    String pathForPassPage = requestHandler.sendNikRequest(pathForNikPage, credentials.accountNumber);
    Map<PathsNames, String> pathsForSessionMapAndPassword = requestHandler.sendPasswordPageRequest(pathForPassPage);
    requestHandler.sendSessionMapRequest(pathsForSessionMapAndPassword.get(SESSION_MAP));
    pauseExecution();
    String smsCodeConfirmationPath = requestHandler.sendPasswordRequest(pathsForSessionMapAndPassword.get(PASSWORD), credentials.password);
    return new FirstAuthFactorToken(smsCodeConfirmationPath);
  }

  public SecondAuthFactorToken secondAuthorizationFactor(FirstAuthFactorToken token, String smsCode) {
    Map<PathsNames, String> paths = requestHandler.sendTokenRequest(token.smsCodeConfirmationPath, smsCode);
    return new SecondAuthFactorToken(paths.get(LOGOUT), paths.get(PRODUCTS));
  }

  public List<AccountDetails> scrapeAccountsDetails(SecondAuthFactorToken token) {
    List<AccountDetails> accountsDetails = requestHandler.scrapeAccountsInformation(token.productsPath);
    requestHandler.sendLogoutRequest(token.logOutPath);
    return accountsDetails;
  }

  private void pauseExecution() {
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      throw new RuntimeException("This should never happen.", e);
    }
  }

  public static class FirstAuthFactorToken {
    private final String smsCodeConfirmationPath;

    private FirstAuthFactorToken(String smsCodeConfirmationPath) {
      this.smsCodeConfirmationPath = smsCodeConfirmationPath;
    }
  }

  public static class SecondAuthFactorToken {
    private final String logOutPath;
    private final String productsPath;

    private SecondAuthFactorToken(String logOutPath, String productsPath) {
      this.logOutPath = logOutPath;
      this.productsPath = productsPath;
    }
  }
}