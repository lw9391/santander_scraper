package scraper.santander.session;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scraper.AccountDetails;
import scraper.InvalidCredentialsException;
import scraper.connections.HttpRequestSender;
import scraper.connections.okhttp.OkHttpRequestsSender;
import scraper.santander.MockWebServerResponsesProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static scraper.santander.PathsNames.*;
import static scraper.santander.session.RequestHandler.RequestSummary;

class RequestsHandlerTest {
  private static RequestHandler requestsHandler;
  private static MockWebServer server;

  @BeforeAll
  static void setUp() throws IOException {
    HttpRequestSender requestSender = new OkHttpRequestsSender();
    SantanderRequestProvider provider = new SantanderRequestProvider("http://localhost:8889");
    requestsHandler = new RequestHandler(requestSender, provider);
    server = new MockWebServer();
    server.start(8889);
  }

  @Test
  void sendLoginPageRequestTest() {
    MockWebServerResponsesProvider.enqueueLoginPage(server);

    RequestSummary requestSummary = requestsHandler.sendLoginPageRequest();

    String scrapedPath = requestSummary.scrapedPaths.get(REDIRECT_XML);
    assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2FI3qkyM18bV", scrapedPath);
    assertEquals("http://localhost:8889/centrum24-web/login", requestSummary.refererForNextRequest);
  }

  @Test
  void sendRedirectXmlRequestTest() {
    MockWebServerResponsesProvider.enqueueXmlWithPathForNikPage(server);
    RequestSummary input = new RequestSummary(Map.of(REDIRECT_XML, "/path"), "login?referer");

    RequestSummary requestSummary = requestsHandler.sendRedirectXmlRequest(input);

    String scrapedPath = requestSummary.scrapedPaths.get(NIK_PAGE);
    assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2Jq7iUgK71WU9KRSiD9ZXtSc6N1yJH61vg", scrapedPath);
  }

  @Test
  void sendNikRequestTest() {
    MockWebServerResponsesProvider.enqueueNikPage(server);
    RequestSummary input = new RequestSummary(Map.of(NIK_PAGE, "/path"), "login?referer");

    RequestSummary requestSummary = requestsHandler.sendNikRequest(input, "111111");

    String scrapedPath = requestSummary.scrapedPaths.get(PASS_PAGE);
    assertEquals("/crypt.brKnpZUkktuD2YnBIm0vpQ/brK0a", scrapedPath);
  }

  @Test
  void sendPasswordPageRequestTest() {
    MockWebServerResponsesProvider.enqueuePasswordPage(server);
    RequestSummary input = new RequestSummary(Map.of(PASS_PAGE, "/path"), "login?referer");

    RequestSummary requestSummary = requestsHandler.sendPasswordPageRequest(input);

    String passwordPath = requestSummary.scrapedPaths.get(PASSWORD);
    String sessionMapPath = requestSummary.scrapedPaths.get(SESSION_MAP);
    assertEquals("/crypt.brKnpZUkktsTyMD4fDym_SLk_R9DvRZrI8wCGgwoOlCfiXbbYM9ZJhVOk0kArlJ9bSYrrEyANi1n2ESVzY5GrffYXOGcjl9xFRMTUc2Ufq8/brK0a", passwordPath);
    assertEquals("/crypt.brKnpZUkktsTyMD4fDym_YLJ6XzBNKJtQSbN-NdTTUaXMzfLzxBZ9EURsRnaBBQxR_jFThmXQm0zbzjNSjxOtMufJ-0MGGRcS6TA4seUNnspto52VanATw/brK0a", sessionMapPath);
    assertEquals("http://localhost:8889/centrum24-web/path", requestSummary.refererForNextRequest);
  }

  @Test
  void sendPasswordRequestTest() {
    MockWebServerResponsesProvider.enqueueTokenPage(server);
    RequestSummary input = new RequestSummary(Map.of(PASSWORD, "/path"), "login?referer");

    RequestSummary requestSummary = requestsHandler.sendPasswordRequest(input, "password");

    String path = requestSummary.scrapedPaths.get(SMS_CODE);
    assertEquals("/crypt.brKnpZUkktvUK1iu4qXMi9bnZ5hezbPacPk819Dz6-8g_orQ4Xq-FjTWUHuDABm_P42aHIYvzffjV0KTJBs5ldLgqxB1y_j3MyHdo2lsyqlmW45BWuI_jcLCy__ihsl4/brK0a", path);
    assertEquals("http://localhost:8889/centrum24-web/path", requestSummary.refererForNextRequest);
  }

  @Test
  void sendSmsCodeRequestTest() {
    MockWebServerResponsesProvider.enqueueDashboardPage(server);
    RequestSummary input = new RequestSummary(Map.of(SMS_CODE, "/path"), "login?referer");

    RequestSummary requestSummary = requestsHandler.sendSmsCodeRequest(input, "111-111");

    String logoutPath = requestSummary.scrapedPaths.get(LOGOUT);
    String productsPath = requestSummary.scrapedPaths.get(PRODUCTS);
    assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jESpGENeIF4xRXVg0UDT17jg", logoutPath);
    assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jEA84g18nLiMe2NjQEn9CgFQ9xGEI9imD2CH07NF_4-1SHx_N-xlO3J6tWfhjyQ0YhzvUgX_37trHGKjggK4JpehiAzGO9SxQrE1fghwvJtv5JhxKwamTKQYMQ0ZoNYzV8EmMYKU9r_Zo", productsPath);
    assertEquals("http://localhost:8889/centrum24-web/path", requestSummary.refererForNextRequest);
  }

  @Test
  void sendSmsCodeRequestInvalidLoginPageThrowsException() {
    MockWebServerResponsesProvider.enqueueInvalidLoginPage(server);
    RequestSummary input = new RequestSummary(Map.of(SMS_CODE, "/path"), "login?referer");

    assertThrows(InvalidCredentialsException.class, () -> requestsHandler.sendSmsCodeRequest(input, "111-111"));
  }

  @Test
  void scrapeAccountsInformationTest() {
    MockWebServerResponsesProvider.enqueueProductsPage(server);
    RequestSummary input = new RequestSummary(Map.of(PRODUCTS, "/path"), "login?referer");

    List<AccountDetails> accountDetailsList = requestsHandler.scrapeAccountsInformation(input);

    assertEquals("112,00 PLN", accountDetailsList.get(0).getBalance());
    assertEquals("Ekstrakonto Plus", accountDetailsList.get(0).getAccountName());
    assertEquals("0,38 PLN", accountDetailsList.get(1).getBalance());
    assertEquals("Konto Oszczednosciowe w PLN", accountDetailsList.get(1).getAccountName());
  }

  @AfterAll
  static void tearDown() throws IOException {
    server.shutdown();
  }
}