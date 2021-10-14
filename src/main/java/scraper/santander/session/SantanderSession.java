package scraper.santander.session;

import scraper.AccountDetails;
import scraper.santander.Credentials;

import java.util.List;

import static scraper.santander.session.RequestHandler.RequestSummary;

public class SantanderSession {
  private final RequestHandler requestHandler;

  public SantanderSession(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  public FirstAuthFactorToken firstAuthorizationFactor(Credentials credentials) {
    RequestSummary loginPageReqSummary = requestHandler.sendLoginPageRequest();
    RequestSummary redirectXmlReqSummary = requestHandler.sendRedirectXmlRequest(loginPageReqSummary);
    pauseExecution();
    RequestSummary nikReqSummary = requestHandler.sendNikRequest(redirectXmlReqSummary, credentials.accountNumber);
    RequestSummary passPageReqSummary = requestHandler.sendPasswordPageRequest(nikReqSummary);
    requestHandler.sendSessionMapRequest(passPageReqSummary);
    pauseExecution();
    RequestSummary smsCodeConfirmationPath = requestHandler.sendPasswordRequest(passPageReqSummary, credentials.password);
    return new FirstAuthFactorToken(smsCodeConfirmationPath);
  }

  public SecondAuthFactorToken secondAuthorizationFactor(FirstAuthFactorToken token, String smsCode) {
    RequestSummary tokenReqSummary = requestHandler.sendSmsCodeRequest(token.passwordReqSummary, smsCode);
    return new SecondAuthFactorToken(tokenReqSummary);
  }

  public List<AccountDetails> scrapeAccountsDetails(SecondAuthFactorToken token) {
    List<AccountDetails> accountsDetails = requestHandler.scrapeAccountsInformation(token.tokenReqSummary);
    requestHandler.sendLogoutRequest(token.tokenReqSummary);
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
    private final RequestSummary passwordReqSummary;

    private FirstAuthFactorToken(RequestSummary passwordReqSummary) {
      this.passwordReqSummary = passwordReqSummary;
    }
  }

  public static class SecondAuthFactorToken {
    private final RequestSummary tokenReqSummary;

    private SecondAuthFactorToken(RequestSummary tokenReqSummary) {
      this.tokenReqSummary = tokenReqSummary;
    }
  }
}