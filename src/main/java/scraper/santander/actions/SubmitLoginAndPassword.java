package scraper.santander.actions;

import org.jsoup.nodes.Document;
import scraper.santander.Credentials;

import java.util.Date;

import static scraper.santander.actions.HttpResponseParser.*;

public class SubmitLoginAndPassword {

  private final SantanderHttpApi exchanges;
  private final Credentials credentials;

  public SubmitLoginAndPassword(SantanderHttpApi session, Credentials credentials) {
    exchanges = session;
    this.credentials = credentials;
  }

  public SubmitSmsCode run() {
    String redirectXmlPath = extractXmlPathFromLoginPage(exchanges.loginPage());
    String nikPagePath = extractPathFromRedirectRequest(redirectXmlPath);
    String passPagePath = extractPasswordPagePathFromNikResponse(exchanges.nik(nikPagePath, credentials.accountNumber()));
    String passwordPath = extractPasswordPathFromPasswordPage(exchanges.passwordPage(passPagePath));
    String smsCodeConfirmationPath = extractSmsCodePathFromPasswordResponse(exchanges.password(passwordPath, credentials.password()));
    return new SubmitSmsCode(exchanges, smsCodeConfirmationPath);
  }

  private String extractPathFromRedirectRequest(String basePath) {
    long timestamp = new Date().getTime();
    String fullPathForXml = basePath + "&_=" + timestamp;
    Document response = exchanges.redirectXml(fullPathForXml);
    return extractNikPagePathFromRedirectXml(response);
  }

}
