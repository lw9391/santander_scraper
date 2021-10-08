package scraper;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import scraper.connections.HttpRequestSender;
import scraper.connections.okhttp.OkHttpRequestsSender;
import scraper.santander.SantanderAccountsScraper;
import scraper.santander.session.RequestHandler;
import scraper.santander.session.SantanderRequestProvider;
import scraper.santander.session.SantanderSession;
import scraper.view.ViewController;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IntegrationTest {
    private static Credentials credentials;
    private static String token;
    private static ViewController viewControllerMock;

    private MockWebServer mockWebServer;

    @BeforeAll
    static void beforeAll() {
        token = "123-123";
        credentials = new Credentials("111111","password");
        viewControllerMock = mock(ViewController.class);
    }

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = MockWebServerProvider.getMockWebServer(credentials, token);
        mockWebServer.start(8889);
    }

    @Test
    void runAndGetExpectedResult() {
        when(viewControllerMock.readInput()).thenReturn(token);
        SantanderAccountsScraper scraper = new SantanderAccountsScraper(initTestSession(), viewControllerMock);
        scraper.run(credentials);
        verify(viewControllerMock,times(1)).displayOutput(expectedResult());
    }

    private static List<AccountDetails> expectedResult() {
        AccountDetails accountOne = new AccountDetails("Ekstrakonto Plus","112,00 PLN");
        AccountDetails accountTwo = new AccountDetails("Konto Oszczednosciowe w PLN","0,38 PLN");
        return List.of(accountOne, accountTwo);
    }

    @Test
    void runWithInvalidCredentialsThrowsException() {
        when(viewControllerMock.readInput()).thenReturn(token);
        Credentials incorrect = new Credentials("111111","anypassword");
        SantanderAccountsScraper scraper = new SantanderAccountsScraper(initTestSession(), viewControllerMock);
        assertThrows(InvalidCredentialsException.class, () -> scraper.run(incorrect));
    }

    @Test
    void runWithInvalidTokenThrowsException() {
        String incorrectToken = "111-111";
        when(viewControllerMock.readInput()).thenReturn(incorrectToken);
        SantanderAccountsScraper scraper = new SantanderAccountsScraper(initTestSession(), viewControllerMock);
        assertThrows(InvalidCredentialsException.class, () -> scraper.run(credentials));
    }

    private SantanderSession initTestSession() {
        HttpRequestSender sender = new OkHttpRequestsSender();
        SantanderRequestProvider provider = new SantanderRequestProvider(readHostFromMockServer());
        RequestHandler requestHandler = new RequestHandler(sender, provider);
        return new SantanderSession(requestHandler);
    }

    private String readHostFromMockServer() {
        String host = mockWebServer.url("").toString();
        return host.substring(0, host.length() - 1); //rid of ending backslash
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}