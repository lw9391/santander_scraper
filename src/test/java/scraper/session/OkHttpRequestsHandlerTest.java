package scraper.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import scraper.AccountDetails;
import scraper.session.connections.OkHttpConnectionHandler;
import scraper.session.connections.ResponseDto;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OkHttpRequestsHandlerTest {
    private RequestHandler requestsHandler;
    private SantanderSession session;
    private OkHttpConnectionHandler connectionHandlerMock;

    @BeforeEach
    void setUp() {
        connectionHandlerMock = mock(OkHttpConnectionHandler.class);
        requestsHandler = new RequestHandler(connectionHandlerMock);
        session = new SantanderSession(requestsHandler);
        session.updateReferer("referer");
    }

    @Test
    void sendRequestForLoginPageParamTest() throws IOException {
        Path path = Paths.get("src/test/resources/http/1logPage.html");
        String logPage = Files.readString(path, StandardCharsets.UTF_8);

        ResponseDto response = mock(ResponseDto.class);
        when(response.getResponseBody()).thenReturn(logPage);
        when(response.getStatus()).thenReturn(200);

        when(response.getRequestUrl()).thenReturn("https://google.pl");
        when(connectionHandlerMock.GETLoginPage()).thenReturn(response);
        String loginPageParam = requestsHandler.sendLoginPageRequest();
        assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2FI3qkyM18bV", loginPageParam);
        assertEquals(session.getCurrentReferer(), "https://google.pl");
    }

    @Test
    void sendRequestForXmlWithParamTest() throws IOException {
        Path path = Paths.get("src/test/resources/http/2redirectXml.xml");
        String xml = Files.readString(path, StandardCharsets.UTF_8);

        ResponseDto responseMock = mock(ResponseDto.class);

        when(responseMock.getResponseBody()).thenReturn(xml);
        when(responseMock.getStatus()).thenReturn(200);

        when(connectionHandlerMock.GETXmlWithPathForNikPage(any(), eq("referer"))).thenReturn(responseMock);

        String response = requestsHandler.sendRedirectXmlRequest("query");
        assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2Jq7iUgK71WU9KRSiD9ZXtSc6N1yJH61vg", response);
    }

    @Test
    void sendNikRequestAndGetXmlRedirectTest() throws IOException {
        Path path = Paths.get("src/test/resources/http/3redirectXml.xml");
        String xml = Files.readString(path, StandardCharsets.UTF_8);

        ResponseDto responseMock = mock(ResponseDto.class);
        when(responseMock.getResponseBody()).thenReturn(xml);
        when(responseMock.getStatus()).thenReturn(200);

        when(connectionHandlerMock.POSTNik("?x=query", "111111", "referer")).thenReturn(responseMock);

        String response = requestsHandler.sendNikRequest("?x=query", "111111");
        assertEquals("/crypt.brKnpZUkktuD2YnBIm0vpQ/brK0a", response);
    }

    @Test
    void sendPasswordPageRequestTest() throws IOException {
        Path path = Paths.get("src/test/resources/http/4loginpage.html");
        String html = Files.readString(path, StandardCharsets.UTF_8);

        ResponseDto passPageMock = mock(ResponseDto.class);

        when(passPageMock.getResponseBody()).thenReturn(html);
        when(passPageMock.getStatus()).thenReturn(200);
        when(passPageMock.getRequestUrl()).thenReturn("https://google.pl");

        when(connectionHandlerMock.GETPasswordPage("path", "referer")).thenReturn(passPageMock);
        URL url = new URL("https://google.pl");


        Map<PathsNames,String> paths = requestsHandler.sendPasswordPageRequest("path");
        assertEquals("/crypt.brKnpZUkktsTyMD4fDym_SLk_R9DvRZrI8wCGgwoOlCfiXbbYM9ZJhVOk0kArlJ9bSYrrEyANi1n2ESVzY5GrffYXOGcjl9xFRMTUc2Ufq8/brK0a", paths.get(PathsNames.PASSWORD));
        assertEquals("/crypt.brKnpZUkktsTyMD4fDym_YLJ6XzBNKJtQSbN-NdTTUaXMzfLzxBZ9EURsRnaBBQxR_jFThmXQm0zbzjNSjxOtMufJ-0MGGRcS6TA4seUNnspto52VanATw/brK0a", paths.get(PathsNames.SESSION_MAP));
        assertEquals(session.getCurrentReferer(), url.toString());
    }

    @Test
    void sendPasswordRequestAndGetAccessPageTest() throws IOException {
        Path path = Paths.get("src/test/resources/http/5tokenpage.html");
        String html = Files.readString(path, StandardCharsets.UTF_8);

        ResponseDto htmlWithTokenMock = mock(ResponseDto.class);
        when(htmlWithTokenMock.getResponseBody()).thenReturn(html);
        when(htmlWithTokenMock.getStatus()).thenReturn(200);
        when(htmlWithTokenMock.getRequestUrl()).thenReturn("https://google.pl");

        when(connectionHandlerMock.POSTPassword("path", "password", "referer")).thenReturn(htmlWithTokenMock);
        URL url = new URL("https://google.pl");

        String response = requestsHandler.sendPasswordRequest("path", "password");
        assertEquals("/crypt.brKnpZUkktvUK1iu4qXMi9bnZ5hezbPacPk819Dz6-8g_orQ4Xq-FjTWUHuDABm_P42aHIYvzffjV0KTJBs5ldLgqxB1y_j3MyHdo2lsyqlmW45BWuI_jcLCy__ihsl4/brK0a", response);
        assertEquals(session.getCurrentReferer(), url.toString());
    }

    @Test
    void sendTokenRequestTest() throws IOException, InvalidCredentialsException {
        Path path = Paths.get("src/test/resources/http/6dashboard.html");
        String html = Files.readString(path, StandardCharsets.UTF_8);

        ResponseDto htmlWithDashboardMock = mock(ResponseDto.class);

        when(htmlWithDashboardMock.getResponseBody()).thenReturn(html);
        when(htmlWithDashboardMock.getStatus()).thenReturn(200);
        when(htmlWithDashboardMock.getRequestUrl()).thenReturn("https://google.pl");
        when(connectionHandlerMock.POSTToken("path", "111-111", "referer")).thenReturn(htmlWithDashboardMock);
        URL url = new URL("https://google.pl");

        var paths = requestsHandler.sendTokenRequest("path", "111-111");
        assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jESpGENeIF4xRXVg0UDT17jg", paths.get(PathsNames.LOGOUT));
        assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jEA84g18nLiMe2NjQEn9CgFQ9xGEI9imD2CH07NF_4-1SHx_N-xlO3J6tWfhjyQ0YhzvUgX_37trHGKjggK4JpehiAzGO9SxQrE1fghwvJtv5JhxKwamTKQYMQ0ZoNYzV8EmMYKU9r_Zo", paths.get(PathsNames.PRODUCTS));
        assertEquals(session.getCurrentReferer(), url.toString());
    }

    @Test
    void scrapAccountsInformationTest() throws IOException {
        Path path = Paths.get("src/test/resources/http/7products.html");
        String html = Files.readString(path, StandardCharsets.UTF_8);

        ResponseDto productsPage = mock(ResponseDto.class);
        when(productsPage.getResponseBody()).thenReturn(html);
        when(productsPage.getStatus()).thenReturn(200);
        when(connectionHandlerMock.GETProductsPage("/path", "referer")).thenReturn(productsPage);
        List<AccountDetails> accountDetailsList = requestsHandler.scrapAccountsInformation("/path");
        assertEquals("112,00 PLN", accountDetailsList.get(0).getBalance());
        assertEquals("Ekstrakonto Plus", accountDetailsList.get(0).getAccountName());
        assertEquals("0,38 PLN", accountDetailsList.get(1).getBalance());
        assertEquals("Konto Oszczednosciowe w PLN", accountDetailsList.get(1).getAccountName());
    }
}