package scraper.santander.session;

import scraper.AccountDetails;
import scraper.InvalidCredentialsException;
import scraper.connections.HttpRequestSender;
import scraper.connections.RequestDto;
import scraper.connections.ResponseDto;
import scraper.santander.DataScraper;
import scraper.santander.PathsNames;
import scraper.util.DataBuilder;

import java.util.Collections;
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

  public RequestSummary sendLoginPageRequest() {
    ProcessedResult<String> processedResult = sendAndProcess(provider::GETLoginPage, sender::sendGET, DataScraper::scrapeXmlPathFromLoginPage);
    String referer = processedResult.response.getRequestUrl();
    return new RequestSummary(Map.of(REDIRECT_XML, processedResult.data), referer);
  }

  public RequestSummary sendRedirectXmlRequest(RequestSummary loginPageReqSummary) {
    long timestamp = new Date().getTime();
    String path = loginPageReqSummary.scrapedPaths.get(REDIRECT_XML);
    String queryForXml = path + "&_=" + timestamp;
    String referer = loginPageReqSummary.refererForNextRequest;

    Supplier<RequestDto> request = () -> provider.GETXmlWithPathForNikPage(queryForXml, referer);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapeNikPagePathFromRedirectXml);
    return new RequestSummary(Map.of(NIK_PAGE, processedResult.data), referer);
  }

  public RequestSummary sendNikRequest(RequestSummary redirectXmlReqSummary, String nik) {
    String path = redirectXmlReqSummary.scrapedPaths.get(NIK_PAGE);
    String referer = redirectXmlReqSummary.refererForNextRequest;
    Supplier<RequestDto> request = () -> provider.POSTNik(path, nik, referer);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendPOST, DataScraper::scrapePasswordPagePathFromNikResponse);
    return new RequestSummary(Map.of(PASS_PAGE, processedResult.data), referer);
  }

  public RequestSummary sendPasswordPageRequest(RequestSummary nikReqSummary) {
    String path = nikReqSummary.scrapedPaths.get(PASS_PAGE);
    String referer = nikReqSummary.refererForNextRequest;
    Supplier<RequestDto> request = () -> provider.GETPasswordPage(path, referer);
    ProcessedResult<Map<PathsNames, String>> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapePathsFromPasswordPage);
    String updatedReferer = processedResult.response.getRequestUrl();
    return new RequestSummary(processedResult.data, updatedReferer);
  }

  public void sendSessionMapRequest(RequestSummary passwordReqSummary) {
    String path = passwordReqSummary.scrapedPaths.get(SESSION_MAP);
    String mapSettings =
            "true%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Ctrue%2Cfalse%2C1300%2C1.5%2C1300%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Ctrue%2Cfalse%2Cfalse";
    long timestamp = new Date().getTime();
    String queryParams = DataBuilder.buildQueryParams("sessionMap", mapSettings, "_", String.valueOf(timestamp));
    String referer = passwordReqSummary.refererForNextRequest;

    Supplier<RequestDto> request = () -> provider.GETSendSessionMap(path + queryParams, referer);
    sendAndProcess(request, sender::sendGET, Function.identity());
  }

  public RequestSummary sendPasswordRequest(RequestSummary passPageReqSummary, String password) {
    String path = passPageReqSummary.scrapedPaths.get(PASSWORD);
    String referer = passPageReqSummary.refererForNextRequest;
    Supplier<RequestDto> request = () -> provider.POSTPassword(path, password, referer);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendPOST, DataScraper::scrapeSmsCodePathFromPasswordResponse);
    String updatedReferer = processedResult.response.getRequestUrl();
    return new RequestSummary(Map.of(SMS_CODE, processedResult.data), updatedReferer);
  }

  public RequestSummary sendSmsCodeRequest(RequestSummary passwordReqSummary, String token) {
    String path = passwordReqSummary.scrapedPaths.get(SMS_CODE);
    String referer= passwordReqSummary.refererForNextRequest;
    Supplier<RequestDto> request = () -> provider.POSTSmsCode(path, token, referer);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendPOST, DataScraper::scrapeInvalidLoginDiv);
    if (!processedResult.data.isEmpty()) {
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
    }
    Map<PathsNames, String> paths = DataScraper.scrapePathsFromDashboardPage(processedResult.response.getResponseBody());

    String updatedReferer = processedResult.response.getRequestUrl();
    return new RequestSummary(paths, updatedReferer);
  }

  public List<AccountDetails> scrapeAccountsInformation(RequestSummary tokenReqSummary) {
    String path = tokenReqSummary.scrapedPaths.get(PRODUCTS);
    String referer = tokenReqSummary.refererForNextRequest;
    Supplier<RequestDto> request = () -> provider.GETProductsPage(path, referer);
    Supplier<RequestDto> logout = () -> provider.GETEmergencyLogout(referer);
    ProcessedResult<List<AccountDetails>> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapeAccountsInformationFromProductsPage, logout);

    return processedResult.data;
  }

  public void sendLogoutRequest(RequestSummary tokenReqSummary) {
    String path = tokenReqSummary.scrapedPaths.get(LOGOUT);
    String referer = tokenReqSummary.refererForNextRequest;
    Supplier<RequestDto> request = () -> provider.GETLogout(path, referer);
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendGET, Function.identity());
    if (!processedResult.response.getRequestUrl().equals(provider.HOST + provider.PATH + provider.LOGOUT)) {
      RequestDto logout = provider.GETEmergencyLogout(referer);
      sender.sendGET(logout);
    }
  }

  private static <T> ProcessedResult<T> sendAndProcess(Supplier<RequestDto> requestSupplier, Function<RequestDto, ResponseDto> httpSendMethod, Function<String, T> scrapingFunction) {
    RequestDto requestDto = requestSupplier.get();
    ResponseDto responseDto = httpSendMethod.apply(requestDto);
    if (!(responseDto.getStatus() == 200)) {
      throw new RuntimeException("Status code error during getting login page.");
    }
    T result = scrapingFunction.apply(responseDto.getResponseBody());
    return new ProcessedResult<>(responseDto, result);
  }

  private static <T> ProcessedResult<T> sendAndProcess(Supplier<RequestDto> requestSupplier, Function<RequestDto, ResponseDto> httpSendMethod, Function<String, T> scrapingFunction, Supplier<RequestDto> logout) {
    RequestDto requestDto = requestSupplier.get();
    ResponseDto responseDto = httpSendMethod.apply(requestDto);
    if (!(responseDto.getStatus() == 200)) {
      httpSendMethod.apply(logout.get());
      throw new RuntimeException("Status code error during getting login page.");
    }
    T result = scrapingFunction.apply(responseDto.getResponseBody());
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

  static class RequestSummary {
    public final Map<PathsNames,String> scrapedPaths;
    public final String refererForNextRequest;

    public RequestSummary(Map<PathsNames, String> scrapedPaths, String refererForNextRequest) {
      this.scrapedPaths = Collections.unmodifiableMap(scrapedPaths);
      this.refererForNextRequest = refererForNextRequest;
    }
  }
}