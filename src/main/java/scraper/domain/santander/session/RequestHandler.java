package scraper.domain.santander.session;

import scraper.domain.AccountDetails;
import scraper.domain.InvalidCredentialsException;
import scraper.domain.http.HttpFetcher;
import scraper.domain.http.Response;

import java.util.Date;
import java.util.List;

import static scraper.domain.santander.DataScraper.*;

public class RequestHandler {

  private final HttpFetcher fetcher;
  private final HttpRequests requests;

  public RequestHandler(HttpFetcher fetcher, HttpRequests requests) {
    this.fetcher = fetcher;
    this.requests = requests;
  }

  public String sendLoginPageRequest() {
    Response response = fetcher.send(requests.loginPage());
    return scrapeXmlPathFromLoginPage(response.body);
  }

  public String sendRedirectXmlRequest(String path) {
    long timestamp = new Date().getTime();
    String fullPathForXml = path + "&_=" + timestamp;

    Response response = fetcher.send(requests.redirectXml(fullPathForXml));
    return scrapeNikPagePathFromRedirectXml(response.body);
  }

  public String sendNikRequest(String path, String nik) {
    Response response = fetcher.send(requests.nik(path, nik));
    return scrapePasswordPagePathFromNikResponse(response.body);
  }

  public String sendPasswordPageRequest(String path) {
    Response response = fetcher.send(requests.passwordPage(path));
    return scrapePasswordPathFromPasswordPage(response.body);
  }

  public String sendPasswordRequest(String path, String password) {
    Response response = fetcher.send(requests.password(path, password));
    return scrapeSmsCodePathFromPasswordResponse(response.body);
  }

  public String sendSmsCodeRequest(String path, String smsCode) {
    Response response = fetcher.send(requests.smsCode(path, smsCode));
    String invalidLoginDiv = scrapeInvalidLoginDiv(response.body);
    if (!invalidLoginDiv.isEmpty())
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");

    return scrapeProductsPathFromDashboardPage(response.body);
  }

  public List<AccountDetails> scrapeAccountsInformation(String path) {
    Response response = fetcher.send(requests.productsPage(path));
    return scrapeAccountsInformationFromProductsPage(response.body);
  }

  public void sendLogoutRequest() {
    fetcher.send(requests.logout());
  }

}