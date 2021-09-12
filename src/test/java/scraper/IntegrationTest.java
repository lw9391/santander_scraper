package scraper;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import scraper.view.ViewController;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testScraper() {
        when(viewControllerMock.readInput()).thenReturn(token);
        String[] args = {credentials.getAccountNumber(), credentials.getPassword()};
        App.setViewController(viewControllerMock);
        App.setLocalhost(true);
        App.main(args);
        verify(viewControllerMock,times(1)).displayOutput(expectedResult());
        assertEquals(11, mockWebServer.getRequestCount());
    }

    private static List<AccountDetails> expectedResult() {
        AccountDetails accountOne = new AccountDetails("Ekstrakonto Plus","112,00 PLN");
        AccountDetails accountTwo = new AccountDetails("Konto Oszczednosciowe w PLN","0,38 PLN");
        return List.of(accountOne, accountTwo);
    }

    @Test
    void testIncorrectPassword() {
        when(viewControllerMock.readInput()).thenReturn(token);
        Credentials incorrect = new Credentials("111111","anypassword");
        String[] args = {incorrect.getAccountNumber(), incorrect.getPassword()};
        App.setViewController(viewControllerMock);
        App.setLocalhost(true);
        assertThrows(InvalidCredentialsException.class, () -> App.main(args));
    }

    @Test
    void testIncorrectToken() {
        String incorrectToken = "111-111";
        when(viewControllerMock.readInput()).thenReturn(incorrectToken);
        String[] args = {credentials.getAccountNumber(), credentials.getPassword()};
        App.setViewController(viewControllerMock);
        App.setLocalhost(true);
        assertThrows(InvalidCredentialsException.class, () -> App.main(args));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}