package scraper.domain.santander;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scraper.domain.AccountDetails;
import scraper.domain.InvalidCredentialsException;
import scraper.domain.http.Fetcher;
import scraper.domain.http.okhttp.OkHttpFetcher;
import scraper.domain.santander.session.RequestHandler;
import scraper.domain.santander.session.HttpRequests;
import scraper.domain.santander.session.Session;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountsScraperTest {

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
    ViewStub viewControllerStub = new ViewStub();
    MockWebServerResponses.enqueueResponses(mockWebServer);
    AccountsScraper scraper = new AccountsScraper(initSession(), viewControllerStub);

    scraper.run(nik, password);

    assertEquals(expectedResult(), viewControllerStub.getStoredDetails());
  }

  private static List<AccountDetails> expectedResult() {
    AccountDetails accountOne = new AccountDetails("Ekstrakonto Plus", "112,00 PLN");
    return List.of(accountOne);
  }

  @Test
  void runWithInvalidCredentialsThrowsException() {
    String nik = "111111";
    String password = "password";
    ViewStub viewControllerStub = new ViewStub();
    MockWebServerResponses.enqueueResponsesForInvalidCredentials(mockWebServer);
    AccountsScraper scraper = new AccountsScraper(initSession(), viewControllerStub);

    assertThrows(InvalidCredentialsException.class, () -> scraper.run(nik, password));
  }

  private static Session initSession() {
    Fetcher sender = new OkHttpFetcher();
    HttpRequests provider = new HttpRequests(readHostFromMockServer());
    RequestHandler requestHandler = new RequestHandler(sender, provider);
    return new Session(requestHandler);
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