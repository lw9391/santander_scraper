package scraper;

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
    private Credentials validCredentials;
    private String validToken;
    private ConnectionHandler connectionHandler;

    @BeforeEach
    void beforeEach() {
        this.validCredentials = new Credentials("111111","password");
        this.validToken = "111-111";
        this.connectionHandler = ConnectionMockProvider.connectionHandlerMock(validCredentials, validToken);
        RequestHandler requestHandler = new RequestHandler(connectionHandler);
        SantanderSession session = new SantanderSession(requestHandler);
        this.scraper = new SantanderAccountsScraper(session);
    }

    @Test
    void scraperTest() {
        scraper.logIn(validCredentials);
        scraper.confirmAccess(validToken);
        List<AccountDetails> accountDetails = scraper.scrapeAccountsInfo();
        assertEquals("112,00 PLN", accountDetails.get(0).getBalance());
        assertEquals("Ekstrakonto Plus", accountDetails.get(0).getAccountName());
        assertEquals("0,38 PLN", accountDetails.get(1).getBalance());
        assertEquals("Konto Oszczednosciowe w PLN", accountDetails.get(1).getAccountName());
        scraper.logOut();
        verify(connectionHandler, times(1)).GETLogout(any(), any());
    }

    @Test
    void scraperTestIncorrectPassword() {
        Credentials incorrect = new Credentials("111111","anypassword");
        scraper.logIn(incorrect);
        assertThrows(InvalidCredentialsException.class, () -> scraper.confirmAccess(validToken));
    }

    @Test
    void scraperTestIncorrectToken() {
        String incorrectToken = "123-123";
        scraper.logIn(validCredentials);
        assertThrows(InvalidCredentialsException.class, () -> scraper.confirmAccess(incorrectToken));
    }
}
