package scraper.santander.actions;

import scraper.santander.Credentials;
import scraper.santander.http.Response;

import java.util.Date;

import static scraper.santander.actions.HttpResponseParser.*;

public class SubmitLoginAndPassword {

  private final HttpExchanges exchanges;
  private final Credentials credentials;

  public SubmitLoginAndPassword(HttpExchanges session, Credentials credentials) {
    exchanges = session;
    this.credentials = credentials;
  }

  public SubmitSmsCode run() {
    String redirectXmlPath = extractXmlPathFromLoginPage(exchanges.loginPage().body);
    String nikPagePath = extractPathFromRedirectRequest(redirectXmlPath);
    String passPagePath = extractPasswordPagePathFromNikResponse(exchanges.nik(nikPagePath, credentials.accountNumber()).body);
    String passwordPath = extractPasswordPathFromPasswordPage(exchanges.passwordPage(passPagePath).body);
    String smsCodeConfirmationPath = extractSmsCodePathFromPasswordResponse(exchanges.password(passwordPath, credentials.password()).body);
    return new SubmitSmsCode(exchanges, smsCodeConfirmationPath);
  }

  private String extractPathFromRedirectRequest(String basePath) {
    long timestamp = new Date().getTime();
    String fullPathForXml = basePath + "&_=" + timestamp;
    Response response = exchanges.redirectXml(fullPathForXml);
    return extractNikPagePathFromRedirectXml(response.body);
  }

}
