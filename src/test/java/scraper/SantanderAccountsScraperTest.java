package scraper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import scraper.session.RequestHandler;
import scraper.session.SantanderSession;
import scraper.session.connections.ConnectionHandler;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SantanderAccountsScraperTest {
    private static SantanderAccountsScraper scraper;
    private static Credentials validCredentials;
    private static String validToken;
    private static ConnectionHandler connectionHandler;

    @BeforeAll
    static void beforeAll() throws IOException {
        validCredentials = new Credentials("111111","password");
        validToken = "111-111";
        connectionHandler = ConnectionMockProvider.connectionHandlerMock(validCredentials, validToken);
        RequestHandler requestHandler = new RequestHandler(connectionHandler);
        SantanderSession session = new SantanderSession(requestHandler);
        scraper = new SantanderAccountsScraper(session);
    }

    @Test
    void scraperTest() throws IOException {
        scraper.logIn(validCredentials);
        scraper.confirmAccess("111-111");
        List<AccountDetails> accountDetails = scraper.scrapAccountsInfo();
        assertEquals("112,00 PLN", accountDetails.get(0).getBalance());
        assertEquals("Ekstrakonto Plus", accountDetails.get(0).getAccountName());
        assertEquals("0,38 PLN", accountDetails.get(1).getBalance());
        assertEquals("Konto Oszczednosciowe w PLN", accountDetails.get(1).getAccountName());
        scraper.logOut();
        verify(connectionHandler, times(1)).GETLogout(any(), any());
    }
}
