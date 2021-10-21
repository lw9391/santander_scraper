package scraper.domain.santander.session;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scraper.domain.AccountDetails;
import scraper.domain.InvalidCredentialsException;
import scraper.domain.http.Fetcher;
import scraper.domain.http.okhttp.OkHttpFetcher;
import scraper.domain.santander.MockWebServerResponses;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestsHandlerTest {

  private static RequestHandler requestsHandler;
  private static MockWebServer server;

  @BeforeAll
  static void setUp() throws IOException {
    Fetcher requestSender = new OkHttpFetcher();
    HttpRequests provider = new HttpRequests("http://localhost:8889/");
    requestsHandler = new RequestHandler(requestSender, provider);
    server = new MockWebServer();
    server.start(8889);
  }

  @Test
  void sendLoginPageRequestTest() {
    MockWebServerResponses.enqueueLoginPage(server);

    String redirectXmlPath = requestsHandler.sendLoginPageRequest();

    assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2FI3qkyM18bV", redirectXmlPath);
  }

  @Test
  void sendRedirectXmlRequestTest() {
    MockWebServerResponses.enqueueXmlWithPathForNikPage(server);

    String nikPath = requestsHandler.sendRedirectXmlRequest("/path");

    assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2Jq7iUgK71WU9KRSiD9ZXtSc6N1yJH61vg", nikPath);
  }

  @Test
  void sendNikRequestTest() {
    MockWebServerResponses.enqueueNikPage(server);

    String passPagePath = requestsHandler.sendNikRequest("path", "111111");

    assertEquals("/crypt.brKnpZUkktuD2YnBIm0vpQ/brK0a", passPagePath);
  }

  @Test
  void sendPasswordPageRequestTest() {
    MockWebServerResponses.enqueuePasswordPage(server);

    String passwordPath = requestsHandler.sendPasswordPageRequest("/path");

    assertEquals("/crypt.brKnpZUkktsTyMD4fDym_SLk_R9DvRZrI8wCGgwoOlCfiXbbYM9ZJhVOk0kArlJ9bSYrrEyANi1n2ESVzY5GrffYXOGcjl9xFRMTUc2Ufq8/brK0a", passwordPath);
  }

  @Test
  void sendPasswordRequestTest() {
    MockWebServerResponses.enqueueTokenPage(server);

    String smsCodePath = requestsHandler.sendPasswordRequest("/path", "password");

    assertEquals("/crypt.brKnpZUkktvUK1iu4qXMi9bnZ5hezbPacPk819Dz6-8g_orQ4Xq-FjTWUHuDABm_P42aHIYvzffjV0KTJBs5ldLgqxB1y_j3MyHdo2lsyqlmW45BWuI_jcLCy__ihsl4/brK0a", smsCodePath);
  }

  @Test
  void sendSmsCodeRequestTest() {
    MockWebServerResponses.enqueueDashboardPage(server);

    String productsPath = requestsHandler.sendSmsCodeRequest("/path", "111-111");

    assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jEA84g18nLiMe2NjQEn9CgFQ9xGEI9imD2CH07NF_4-1SHx_N-xlO3J6tWfhjyQ0YhzvUgX_37trHGKjggK4JpehiAzGO9SxQrE1fghwvJtv5JhxKwamTKQYMQ0ZoNYzV8EmMYKU9r_Zo", productsPath);
  }

  @Test
  void sendSmsCodeRequestInvalidLoginPageThrowsException() {
    MockWebServerResponses.enqueueInvalidLoginPage(server);

    assertThrows(InvalidCredentialsException.class, () -> requestsHandler.sendSmsCodeRequest("path", "111-111"));
  }

  @Test
  void scrapeAccountsInformationTest() {
    MockWebServerResponses.enqueueProductsPage(server);

    List<AccountDetails> accountDetailsList = requestsHandler.scrapeAccountsInformation("path");

    assertEquals("112,00 PLN", accountDetailsList.get(0).balance());
    assertEquals("Ekstrakonto Plus", accountDetailsList.get(0).accountName());
    assertEquals("0,38 PLN", accountDetailsList.get(1).balance());
    assertEquals("Konto Oszczednosciowe w PLN", accountDetailsList.get(1).accountName());
  }

  @AfterAll
  static void tearDown() throws IOException {
    server.shutdown();
  }

}