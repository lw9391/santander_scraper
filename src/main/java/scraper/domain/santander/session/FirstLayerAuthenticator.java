package scraper.domain.santander.session;

import scraper.domain.http.Response;
import scraper.domain.santander.Credentials;

import java.util.Date;

import static scraper.domain.santander.session.HttpResponseParser.*;

public class FirstLayerAuthenticator {

  private final HttpExchanges exchanges;
  private final Credentials credentials;

  public FirstLayerAuthenticator(HttpExchanges session, Credentials credentials) {
    this.exchanges = session;
    this.credentials = credentials;
  }

  public SecondLayerAuthenticator authenticate() {
    String redirectXmlPath = extractXmlPathFromLoginPage(exchanges.loginPage().body);
    String nikPagePath = extractPathFromRedirectRequest(redirectXmlPath);
    String passPagePath = extractPasswordPagePathFromNikResponse(exchanges.nik(nikPagePath, credentials.accountNumber()).body);
    String passwordPath = extractPasswordPathFromPasswordPage(exchanges.passwordPage(passPagePath).body);
    String smsCodeConfirmationPath = extractSmsCodePathFromPasswordResponse(exchanges.password(passwordPath, credentials.password()).body);
    return new SecondLayerAuthenticator(exchanges, smsCodeConfirmationPath);
  }

  private String extractPathFromRedirectRequest(String basePath) {
    long timestamp = new Date().getTime();
    String fullPathForXml = basePath + "&_=" + timestamp;
    Response response = exchanges.redirectXml(fullPathForXml);
    return extractNikPagePathFromRedirectXml(response.body);
  }

}
