package scraper.domain.santander.session;

import scraper.domain.AccountDetails;
import scraper.domain.InvalidCredentialsException;
import scraper.domain.http.HttpFetcher;
import scraper.domain.http.Request;
import scraper.domain.http.Response;
import scraper.domain.santander.DataScraper;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RequestHandler {

  private final HttpFetcher sender;
  private final HttpRequests requests;

  public RequestHandler(HttpFetcher sender, HttpRequests requests) {
    this.sender = sender;
    this.requests = requests;
  }

  public String sendLoginPageRequest() {
    return sendRequestAndProcessResponseBody(requests::loginPage, sender::sendGET, DataScraper::scrapeXmlPathFromLoginPage);
  }

  public String sendRedirectXmlRequest(String path) {
    long timestamp = new Date().getTime();
    String fullPathForXml = path + "&_=" + timestamp;

    Supplier<Request> request = () -> requests.redirectXml(fullPathForXml);
    return sendRequestAndProcessResponseBody(request, sender::sendGET, DataScraper::scrapeNikPagePathFromRedirectXml);
  }

  public String sendNikRequest(String path, String nik) {
    Supplier<Request> request = () -> requests.nik(path, nik);
    return sendRequestAndProcessResponseBody(request, sender::sendPOST, DataScraper::scrapePasswordPagePathFromNikResponse);
  }

  public String sendPasswordPageRequest(String path) {
    Supplier<Request> request = () -> requests.passwordPage(path);
    return sendRequestAndProcessResponseBody(request, sender::sendGET, DataScraper::scrapePathsFromPasswordPage);
  }

  public String sendPasswordRequest(String path, String password) {
    Supplier<Request> request = () -> requests.password(path, password);
    return sendRequestAndProcessResponseBody(request, sender::sendPOST, DataScraper::scrapeSmsCodePathFromPasswordResponse);
  }

  public String sendSmsCodeRequest(String path, String smsCode) {
    Supplier<Request> request = () -> requests.smsCode(path, smsCode);
    String responseBody = sendRequestAndProcessResponseBody(request, sender::sendPOST, UnaryOperator.identity());
    String invalidLoginDiv = DataScraper.scrapeInvalidLoginDiv(responseBody);
    if (!invalidLoginDiv.isEmpty())
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");

    return DataScraper.scrapePathsFromDashboardPage(responseBody);
  }

  public List<AccountDetails> scrapeAccountsInformation(String path) {
    Supplier<Request> request = () -> requests.productsPage(path);
    Supplier<Request> logout = requests::logout;

    return sendRequestAndProcessResponseBody(request, sender::sendGET, DataScraper::scrapeAccountsInformationFromProductsPage, logout);
  }

  public void sendLogoutRequest() {
    Supplier<Request> request = requests::logout;
    sendRequestAndProcessResponseBody(request, sender::sendGET, UnaryOperator.identity());
  }

  private static String sendRequestAndProcessResponseBody(Supplier<Request> requestSupplier, Function<Request, Response> httpSendMethod, UnaryOperator<String> scrapingFunction) {
    Request request = requestSupplier.get();
    Response response = httpSendMethod.apply(request);
    if (!(response.status == 200))
      throw new RuntimeException("Status code error during getting login page.");

    return scrapingFunction.apply(response.responseBody);
  }

  private static List<AccountDetails> sendRequestAndProcessResponseBody(Supplier<Request> requestSupplier, Function<Request, Response> httpSendMethod, Function<String, List<AccountDetails>> scrapingFunction, Supplier<Request> logout) {
    Request request = requestSupplier.get();
    Response response = httpSendMethod.apply(request);
    if (!(response.status == 200)) {
      httpSendMethod.apply(logout.get());
      throw new RuntimeException("Status code error during getting login page.");
    }
    return scrapingFunction.apply(response.responseBody);
  }

}