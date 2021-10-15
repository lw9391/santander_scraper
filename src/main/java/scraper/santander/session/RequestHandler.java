package scraper.santander.session;

import scraper.AccountDetails;
import scraper.InvalidCredentialsException;
import scraper.connections.HttpRequestSender;
import scraper.connections.RequestDto;
import scraper.connections.ResponseDto;
import scraper.santander.DataScraper;
import scraper.santander.PathsNames;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static scraper.santander.PathsNames.*;

public class RequestHandler {

  private final HttpRequestSender sender;
  private final SantanderHttpRequestsProvider provider;

  public RequestHandler(HttpRequestSender sender, SantanderHttpRequestsProvider provider) {
    this.sender = sender;
    this.provider = provider;
  }

  public String sendLoginPageRequest() {
    ProcessedResult<String> processedResult = sendAndProcess(provider::GETLoginPage, sender::sendGET, DataScraper::scrapeXmlPathFromLoginPage);
    return processedResult.data;
  }

  public String sendRedirectXmlRequest(String path) {
    long timestamp = new Date().getTime();
    String fullPathForXml = path + "&_=" + timestamp;

    Supplier<RequestDto> request = () -> provider.GETXmlWithPathForNikPage(fullPathForXml);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapeNikPagePathFromRedirectXml);
    return processedResult.data;
  }

  public String sendNikRequest(String path, String nik) {
    Supplier<RequestDto> request = () -> provider.POSTNik(path, nik);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendPOST, DataScraper::scrapePasswordPagePathFromNikResponse);
    return processedResult.data;
  }

  public String sendPasswordPageRequest(String path) {
    Supplier<RequestDto> request = () -> provider.GETPasswordPage(path);
    ProcessedResult<Map<PathsNames, String>> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapePathsFromPasswordPage);
    return processedResult.data.get(PASSWORD);
  }

  public String sendPasswordRequest(String path, String password) {
    Supplier<RequestDto> request = () -> provider.POSTPassword(path, password);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendPOST, DataScraper::scrapeSmsCodePathFromPasswordResponse);
    return processedResult.data;
  }

  public String sendSmsCodeRequest(String path, String smsCode) {
    Supplier<RequestDto> request = () -> provider.POSTSmsCode(path, smsCode);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendPOST, DataScraper::scrapeInvalidLoginDiv);
    if (!processedResult.data.isEmpty()) {
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
    }
    return DataScraper.scrapePathsFromDashboardPage(processedResult.response.responseBody).get(PRODUCTS);
  }

  public List<AccountDetails> scrapeAccountsInformation(String path) {
    Supplier<RequestDto> request = () -> provider.GETProductsPage(path);
    Supplier<RequestDto> logout = provider::GETLogout;
    ProcessedResult<List<AccountDetails>> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapeAccountsInformationFromProductsPage, logout);

    return processedResult.data;
  }

  public void sendLogoutRequest() {
    Supplier<RequestDto> request = provider::GETLogout;
    sendAndProcess(request, sender::sendGET, Function.identity());
  }

  private static <T> ProcessedResult<T> sendAndProcess(Supplier<RequestDto> requestSupplier, Function<RequestDto, ResponseDto> httpSendMethod, Function<String, T> scrapingFunction) {
    RequestDto requestDto = requestSupplier.get();
    ResponseDto responseDto = httpSendMethod.apply(requestDto);
    if (!(responseDto.status == 200)) {
      throw new RuntimeException("Status code error during getting login page.");
    }
    T result = scrapingFunction.apply(responseDto.responseBody);
    return new ProcessedResult<>(responseDto, result);
  }

  private static <T> ProcessedResult<T> sendAndProcess(Supplier<RequestDto> requestSupplier, Function<RequestDto, ResponseDto> httpSendMethod, Function<String, T> scrapingFunction, Supplier<RequestDto> logout) {
    RequestDto requestDto = requestSupplier.get();
    ResponseDto responseDto = httpSendMethod.apply(requestDto);
    if (!(responseDto.status == 200)) {
      httpSendMethod.apply(logout.get());
      throw new RuntimeException("Status code error during getting login page.");
    }
    T result = scrapingFunction.apply(responseDto.responseBody);
    return new ProcessedResult<>(responseDto, result);
  }

  private static class ProcessedResult<T> {
    public final ResponseDto response;
    public final T data;

    public ProcessedResult(ResponseDto response, T data) {
      this.response = response;
      this.data = data;
    }
  }

}