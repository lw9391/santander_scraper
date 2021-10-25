package scraper.santander.santander;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scraper.santander.AccountDetails;
import scraper.santander.AccountsScraper;
import scraper.santander.InvalidCredentialsException;
import scraper.santander.actions.HttpExchanges;
import scraper.santander.http.Fetcher;
import scraper.santander.http.okhttp.OkHttpFetcher;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountsScraperTest {

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

  private static HttpExchanges initSession() {
    Fetcher fetcher = new OkHttpFetcher();
    return new HttpExchanges(readHostFromMockServer(), fetcher);
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