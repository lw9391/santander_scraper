package scraper.santander;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scraper.AccountDetails;
import scraper.InvalidCredentialsException;
import scraper.connections.HttpRequestSender;
import scraper.connections.okhttp.OkHttpRequestsSender;
import scraper.santander.session.RequestHandler;
import scraper.santander.session.SantanderHttpRequestsProvider;
import scraper.santander.session.SantanderSession;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SantanderAccountsScraperTest {

  private static MockWebServer mockWebServer;

  @BeforeAll
  static void initServer() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start(8889);
  }

  @Test
  void runAndGetExpectedResult() {
    String nik = "111111";
    String password = "password";
    ViewControllerStub viewControllerStub = new ViewControllerStub();
    MockWebServerResponsesProvider.enqueueResponses(mockWebServer);
    SantanderAccountsScraper scraper = new SantanderAccountsScraper(initSession(), viewControllerStub);

    scraper.run(nik, password);

    assertEquals(expectedResult(), viewControllerStub.getStoredDetails());
  }

  private static List<AccountDetails> expectedResult() {
    AccountDetails accountOne = new AccountDetails("Ekstrakonto Plus", "112,00 PLN");
    AccountDetails accountTwo = new AccountDetails("Konto Oszczednosciowe w PLN", "0,38 PLN");
    return List.of(accountOne, accountTwo);
  }

  @Test
  void runWithInvalidCredentialsThrowsException() {
    String nik = "111111";
    String password = "password";
    ViewControllerStub viewControllerStub = new ViewControllerStub();
    MockWebServerResponsesProvider.enqueueResponsesForInvalidCredentials(mockWebServer);
    SantanderAccountsScraper scraper = new SantanderAccountsScraper(initSession(), viewControllerStub);

    assertThrows(InvalidCredentialsException.class, () -> scraper.run(nik, password));
  }

  private static SantanderSession initSession() {
    HttpRequestSender sender = new OkHttpRequestsSender();
    SantanderHttpRequestsProvider provider = new SantanderHttpRequestsProvider(readHostFromMockServer());
    RequestHandler requestHandler = new RequestHandler(sender, provider);
    return new SantanderSession(requestHandler);
  }

  private static String readHostFromMockServer() {
    String host = mockWebServer.url("").toString();
    return host.substring(0, host.length() - 1); //rid of ending backslash
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

}