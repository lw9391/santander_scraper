package scraper.santander.session;

import scraper.AccountDetails;
import scraper.InvalidCredentialsException;
import scraper.connections.HttpRequestSender;
import scraper.connections.RequestDto;
import scraper.connections.ResponseDto;
import scraper.santander.DataScraper;
import scraper.santander.PathsNames;
import scraper.util.DataBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class RequestHandler {
  private final HttpRequestSender sender;
  private final SantanderRequestProvider provider;

  private SantanderSession session;

  public RequestHandler(HttpRequestSender sender, SantanderRequestProvider provider, SantanderSession session) {
    this.session = session;
    this.sender = sender;
    this.provider = provider;
  }

  public RequestHandler(HttpRequestSender sender, SantanderRequestProvider provider) {
    this.sender = sender;
    this.provider = provider;
  }

  public String sendLoginPageRequest() {
    ProcessedResult<String> processedResult = sendAndProcess(provider::GETLoginPage, sender::sendGET, DataScraper::scrapeXmlPathFromLoginPage);
    session.updateReferer(processedResult.response.getRequestUrl());
    return processedResult.data;
  }

  public String sendRedirectXmlRequest(String queryParam) {
    long timestamp = new Date().getTime();
    String queryForXml = queryParam + "&_=" + timestamp;

    Supplier<RequestDto> request = () -> provider.GETXmlWithPathForNikPage(queryForXml, session.getCurrentReferer());
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapeNikPagePathFromRedirectXml);
    return processedResult.data;
  }

  public String sendNikRequest(String queryParam, String nik) {
    Supplier<RequestDto> request = () -> provider.POSTNik(queryParam, nik, session.getCurrentReferer());
    return sendAndProcess(request, sender::sendPOST, DataScraper::scrapePasswordPagePathFromNikResponse).data;
  }

  public Map<PathsNames, String> sendPasswordPageRequest(String path) {
    Supplier<RequestDto> request = () -> provider.GETPasswordPage(path, session.getCurrentReferer());
    ProcessedResult<Map<PathsNames, String>> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapePathsFromPasswordPage);
    session.updateReferer(processedResult.response.getRequestUrl());
    return processedResult.data;
  }

  public void sendSessionMapRequest(String path) {
    String mapSettings =
            "true%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Ctrue%2Cfalse%2C1300%2C1.5%2C1300%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Ctrue%2Cfalse%2Cfalse";
    long timestamp = new Date().getTime();
    String queryParams = DataBuilder.buildQueryParams("sessionMap", mapSettings, "_", String.valueOf(timestamp));

    Supplier<RequestDto> request = () -> provider.GETSendSessionMap(path + queryParams, session.getCurrentReferer());
    sendAndProcess(request, sender::sendGET, Function.identity());
  }

  public String sendPasswordRequest(String path, String password) {
    Supplier<RequestDto> request = () -> provider.POSTPassword(path, password, session.getCurrentReferer());
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendPOST, DataScraper::scrapeTokenPathFromPasswordResponse);
    session.updateReferer(processedResult.response.getRequestUrl());
    return processedResult.data;
  }

  public Map<PathsNames, String> sendTokenRequest(String tokenConfirmationPath, String token) {
    Supplier<RequestDto> request = () -> provider.POSTToken(tokenConfirmationPath, token, session.getCurrentReferer());
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendPOST, DataScraper::scrapeInvalidLoginDiv);
    if (!processedResult.data.isEmpty()) {
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
    }
    Map<PathsNames, String> paths = DataScraper.scrapePathsFromDashboardPage(processedResult.response.getResponseBody());

    session.updateReferer(processedResult.response.getRequestUrl());
    return paths;
  }

  public List<AccountDetails> scrapeAccountsInformation(String path) {
    Supplier<RequestDto> request = () -> provider.GETProductsPage(path, session.getCurrentReferer());
    Supplier<RequestDto> logout = () -> provider.GETEmergencyLogout(session.getCurrentReferer());
    ProcessedResult<List<AccountDetails>> processedResult = sendAndProcess(request, sender::sendGET, DataScraper::scrapeAccountsInformationFromProductsPage, logout);

    return processedResult.data;
  }

  public void sendLogoutRequest(String query) {
    Supplier<RequestDto> request = () -> provider.GETLogout(query, session.getCurrentReferer());
    ProcessedResult<String> processedResult = sendAndProcess(request, sender::sendGET, Function.identity());
    if (!processedResult.response.getRequestUrl().equals(provider.HOST + provider.PATH + provider.LOGOUT)) {
      RequestDto logout = provider.GETEmergencyLogout(session.getCurrentReferer());
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

  public void setSession(SantanderSession session) {
    this.session = session;
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