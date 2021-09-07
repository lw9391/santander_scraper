package scraper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import scraper.session.InvalidCredentialsException;
import scraper.session.RequestHandler;
import scraper.session.SantanderSession;
import scraper.session.connections.ConnectionHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SantanderAccountsScraperTest {
    private SantanderAccountsScraper scraper;
    private static Credentials validCredentials;
    private static String validToken;
    private ConnectionHandler connectionHandler;
    private static ViewController viewControllerMock;

    @BeforeAll
    static void beforeAll() {
        validCredentials = new Credentials("111111","password");
        validToken = "111-111";
        viewControllerMock = mock(ViewController.class);
    }

    @BeforeEach
    void beforeEach() {
        this.connectionHandler = ConnectionMockProvider.connectionHandlerMock(validCredentials, validToken);
        RequestHandler requestHandler = new RequestHandler(connectionHandler);
        SantanderSession session = new SantanderSession(requestHandler);
        this.scraper = new SantanderAccountsScraper(session, viewControllerMock);
    }

    @Test
    void scraperTest() {
        when(viewControllerMock.readInput()).thenReturn(validToken);

        scraper.logIn(validCredentials);
        List<AccountDetails> accountDetails = scraper.scrapeAccountsInfo();
        scraper.logOut();

        assertEquals("112,00 PLN", accountDetails.get(0).getBalance());
        assertEquals("Ekstrakonto Plus", accountDetails.get(0).getAccountName());
        assertEquals("0,38 PLN", accountDetails.get(1).getBalance());
        assertEquals("Konto Oszczednosciowe w PLN", accountDetails.get(1).getAccountName());

        verify(connectionHandler, times(1)).GETLogout(any(), any());
    }

    @Test
    void scraperTestIncorrectPassword() {
        when(viewControllerMock.readInput()).thenReturn(validToken);
        Credentials incorrect = new Credentials("111111","anypassword");
        assertThrows(InvalidCredentialsException.class, () -> scraper.logIn(incorrect));
    }

    @Test
    void scraperTestIncorrectToken() {
        String incorrectToken = "123-123";
        when(viewControllerMock.readInput()).thenReturn(incorrectToken);
        assertThrows(InvalidCredentialsException.class, () -> scraper.logIn(validCredentials));
    }
}
