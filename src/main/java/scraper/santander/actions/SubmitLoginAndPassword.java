package scraper.santander.actions;

import org.jsoup.nodes.Document;
import scraper.santander.Credentials;

import java.util.Date;

import static scraper.santander.actions.ResponseParser.*;

public class SubmitLoginAndPassword {

  private final SantanderHttpApi api;
  private final Credentials credentials;

  public SubmitLoginAndPassword(SantanderHttpApi session, Credentials credentials) {
    api = session;
    this.credentials = credentials;
  }

  public SubmitSmsCode run() {
    String redirectXmlPath = extractXmlPath(api.loginPage());
    String nikPagePath = extractPathFromRedirectRequest(redirectXmlPath);
    String passPagePath = extractPasswordPagePath(api.nik(nikPagePath, credentials.accountNumber()));
    String passwordPath = extractPasswordPath(api.passwordPage(passPagePath));
    String smsCodeConfirmationPath = extractSmsCodePath(api.password(passwordPath, credentials.password()));
    return new SubmitSmsCode(api, smsCodeConfirmationPath);
  }

  private String extractPathFromRedirectRequest(String basePath) {
    long timestamp = new Date().getTime();
    String fullPathForXml = basePath + "&_=" + timestamp;
    Document response = api.redirectXml(fullPathForXml);
    return extractNikPagePath(response);
  }

}
