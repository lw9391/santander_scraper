package scraper.santander;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import scraper.santander.actions.SantanderHttpApi;
import scraper.santander.http.okhttp.OkHttpFetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountsScraperTest {

  private static MockWebServer mockWebServer;

  @BeforeAll
  static void initServer() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start(8889);
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void runAndGetExpectedResult() {
    var view = new ViewStub();
    SantanderMock.logInAndReturnAccounts(mockWebServer);
    AccountsScraper scraper = new AccountsScraper(createSantanderApi(), view);

    scraper.run("111111", "password");

    assertEquals(List.of(new Account("Ekstrakonto Plus", "112,00 PLN")), view.getStoredDetails());
  }

  @Test
  void runWithInvalidCredentialsThrowsException() {
    SantanderMock.invalidCredentials(mockWebServer);
    AccountsScraper scraper = new AccountsScraper(createSantanderApi(), new ViewStub());

    assertThrows(InvalidCredentialsException.class, () -> scraper.run("111111", "password"));
  }

  @ParameterizedTest
  @MethodSource("invalidCredentials")
  void throwsOnInvalidCredentials(String nik, String password) {
    AccountsScraper scraper = new AccountsScraper(createSantanderApi(), new ViewStub());
    assertThrows(InvalidCredentialsException.class, () -> scraper.run(nik, password));
  }

  static Stream<Arguments> invalidCredentials() {
    return Stream.of(
            Arguments.arguments("11", "password"),
            Arguments.arguments("111111", "pa"),
            Arguments.arguments("111111111111111111111", "password"),
            Arguments.arguments("111111", "tooLongPasswordProvided")
    );
  }

  private static SantanderHttpApi createSantanderApi() {
    return new SantanderHttpApi(readHostFromMockServer(), new OkHttpFetcher());
  }

  private static String readHostFromMockServer() {
    String host = mockWebServer.url("").toString();
    return host.substring(0, host.length() - 1); //rid of ending backslash
  }

  public static class ViewStub implements View {

    private final List<Account> storedDetails;

    ViewStub() {
      storedDetails = new ArrayList<>();
    }

    @Override
    public String readSmsCode() {
      return "111-111";
    }

    @Override
    public void display(List<Account> accountsList) {
      storedDetails.addAll(accountsList);
    }

    List<Account> getStoredDetails() {
      return storedDetails;
    }

  }
}