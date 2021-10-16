package scraper.santander.session;

import scraper.AccountDetails;
import scraper.InvalidCredentialsException;
import scraper.connections.HttpRequestSender;
import scraper.connections.RequestDto;
import scraper.connections.ResponseDto;
import scraper.santander.DataScraper;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RequestHandler {

  private final HttpRequestSender sender;
  private final SantanderHttpRequestsProvider provider;

  public RequestHandler(HttpRequestSender sender, SantanderHttpRequestsProvider provider) {
    this.sender = sender;
    this.provider = provider;
  }

  public String sendLoginPageRequest() {
    return sendRequestAndProcessResponseBody(provider::GETLoginPage, sender::sendGET, DataScraper::scrapeXmlPathFromLoginPage);
  }

  public String sendRedirectXmlRequest(String path) {
    long timestamp = new Date().getTime();
    String fullPathForXml = path + "&_=" + timestamp;

    Supplier<RequestDto> request = () -> provider.GETXmlWithPathForNikPage(fullPathForXml);
    return sendRequestAndProcessResponseBody(request, sender::sendGET, DataScraper::scrapeNikPagePathFromRedirectXml);
  }

  public String sendNikRequest(String path, String nik) {
    Supplier<RequestDto> request = () -> provider.POSTNik(path, nik);
    return sendRequestAndProcessResponseBody(request, sender::sendPOST, DataScraper::scrapePasswordPagePathFromNikResponse);
  }

  public String sendPasswordPageRequest(String path) {
    Supplier<RequestDto> request = () -> provider.GETPasswordPage(path);
    return sendRequestAndProcessResponseBody(request, sender::sendGET, DataScraper::scrapePathsFromPasswordPage);
  }

  public String sendPasswordRequest(String path, String password) {
    Supplier<RequestDto> request = () -> provider.POSTPassword(path, password);
    return sendRequestAndProcessResponseBody(request, sender::sendPOST, DataScraper::scrapeSmsCodePathFromPasswordResponse);
  }

  public String sendSmsCodeRequest(String path, String smsCode) {
    Supplier<RequestDto> request = () -> provider.POSTSmsCode(path, smsCode);
    String responseBody = sendRequestAndProcessResponseBody(request, sender::sendPOST, UnaryOperator.identity());
    String invalidLoginDiv = DataScraper.scrapeInvalidLoginDiv(responseBody);
    if (!invalidLoginDiv.isEmpty())
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");

    return DataScraper.scrapePathsFromDashboardPage(responseBody);
  }

  public List<AccountDetails> scrapeAccountsInformation(String path) {
    Supplier<RequestDto> request = () -> provider.GETProductsPage(path);
    Supplier<RequestDto> logout = provider::GETLogout;

    return sendRequestAndProcessResponseBody(request, sender::sendGET, DataScraper::scrapeAccountsInformationFromProductsPage, logout);
  }

  public void sendLogoutRequest() {
    Supplier<RequestDto> request = provider::GETLogout;
    sendRequestAndProcessResponseBody(request, sender::sendGET, UnaryOperator.identity());
  }

  private static String sendRequestAndProcessResponseBody(Supplier<RequestDto> requestSupplier, Function<RequestDto, ResponseDto> httpSendMethod, UnaryOperator<String> scrapingFunction) {
    RequestDto requestDto = requestSupplier.get();
    ResponseDto responseDto = httpSendMethod.apply(requestDto);
    if (!(responseDto.status == 200))
      throw new RuntimeException("Status code error during getting login page.");

    return scrapingFunction.apply(responseDto.responseBody);
  }

  private static List<AccountDetails> sendRequestAndProcessResponseBody(Supplier<RequestDto> requestSupplier, Function<RequestDto, ResponseDto> httpSendMethod, Function<String, List<AccountDetails>> scrapingFunction, Supplier<RequestDto> logout) {
    RequestDto requestDto = requestSupplier.get();
    ResponseDto responseDto = httpSendMethod.apply(requestDto);
    if (!(responseDto.status == 200)) {
      httpSendMethod.apply(logout.get());
      throw new RuntimeException("Status code error during getting login page.");
    }
    return scrapingFunction.apply(responseDto.responseBody);
  }

}