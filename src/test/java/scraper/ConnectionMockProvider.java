package scraper;

import org.mockito.stubbing.Answer;
import scraper.session.connections.ResponseDto;
import scraper.session.connections.SantanderConnectionHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static scraper.SantanderAccountsScraper.HOST;
import static scraper.SantanderAccountsScraper.PATH;
import static scraper.SantanderAccountsScraper.DASHBOARD_PATH;
import static org.mockito.Mockito.*;


public class ConnectionMockProvider {
    public static SantanderConnectionHandler connectionHandlerMock(Credentials correctCredentials, String correctToken) {
        SantanderConnectionHandler connectionHandler = mock(SantanderConnectionHandler.class);

        final var credVerStorage = new Object() {
            boolean isNikCorrect = false;
            boolean isPassCorrect = false;
            boolean isTokenCorrect = false;
        };

        String loginPage = testDataSupplier("src/test/resources/http/1logPage.html");
        String redirectXml = testDataSupplier("src/test/resources/http/2redirectXml.xml");
        String xmlWithPassPage = testDataSupplier("src/test/resources/http/3redirectXml.xml");
        String passPage = testDataSupplier("src/test/resources/http/4loginpage.html");
        String tokenPage = testDataSupplier("src/test/resources/http/5tokenpage.html");
        String dashboard = testDataSupplier("src/test/resources/http/6dashboard.html");
        String products = testDataSupplier("src/test/resources/http/7products.html");
        String invalidCrud = testDataSupplier("src/test/resources/http/8invalidCrudPage.html");

        String xmlPath = "/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2FI3qkyM18bV";
        String nikPath = "/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2Jq7iUgK71WU9KRSiD9ZXtSc6N1yJH61vg";
        String passPagePath = "/crypt.brKnpZUkktuD2YnBIm0vpQ/brK0a";
        String sessionMapPath = "/crypt.brKnpZUkktsTyMD4fDym_YLJ6XzBNKJtQSbN-NdTTUaXMzfLzxBZ9EURsRnaBBQxR_jFThmXQm0zbzjNSjxOtMufJ-0MGGRcS6TA4seUNnspto52VanATw/brK0a";
        String passwordPath = "/crypt.brKnpZUkktsTyMD4fDym_SLk_R9DvRZrI8wCGgwoOlCfiXbbYM9ZJhVOk0kArlJ9bSYrrEyANi1n2ESVzY5GrffYXOGcjl9xFRMTUc2Ufq8/brK0a";
        String tokenPath = "/crypt.brKnpZUkktvUK1iu4qXMi9bnZ5hezbPacPk819Dz6-8g_orQ4Xq-FjTWUHuDABm_P42aHIYvzffjV0KTJBs5ldLgqxB1y_j3MyHdo2lsyqlmW45BWuI_jcLCy__ihsl4/brK0a";
        String productsPath = "/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jEA84g18nLiMe2NjQEn9CgFQ9xGEI9imD2CH07NF_4-1SHx_N-xlO3J6tWfhjyQ0YhzvUgX_37trHGKjggK4JpehiAzGO9SxQrE1fghwvJtv5JhxKwamTKQYMQ0ZoNYzV8EmMYKU9r_Zo";
        String logoutPath = "/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jESpGENeIF4xRXVg0UDT17jg";

        when(connectionHandler.GETLoginPage())
                .thenReturn(responseOf(loginPage,HOST + PATH + "/login"));

        when(connectionHandler.GETXmlWithPathForNikPage(contains(xmlPath),eq(HOST + PATH + "/login")))
                .thenReturn(responseOf(redirectXml,HOST + PATH + xmlPath));

        when(connectionHandler.POSTNik(eq(nikPath), any(), eq(HOST + PATH + "/login")))
                .thenAnswer((Answer<ResponseDto>) invocation -> {
                    String providedNik = invocation.getArgument(1);
                    if (providedNik.equals(correctCredentials.getAccountNumber())) {
                        credVerStorage.isNikCorrect = true;
                    }
                    return responseOf(xmlWithPassPage,HOST + PATH + nikPath);
                });

        when(connectionHandler.GETPasswordPage(passPagePath,HOST + PATH + "/login"))
                .thenReturn(responseOf(passPage,HOST + PATH + passPagePath));

        when(connectionHandler.GETSendSessionMap(contains(sessionMapPath),eq(HOST + PATH + passPagePath)))
                .thenReturn(responseOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ajax-response></ajax-response>",HOST + PATH + sessionMapPath));

        when(connectionHandler.POSTPassword(eq(passwordPath), any(), eq(HOST + PATH + passPagePath)))
                .thenAnswer((Answer<ResponseDto>) invocation -> {
                    String providedPassword = invocation.getArgument(1);
                    if (providedPassword.equals(correctCredentials.getPassword())) {
                        credVerStorage.isPassCorrect = true;
                    }
                    return responseOf(tokenPage,HOST + PATH + passwordPath);
                });

        when(connectionHandler.POSTToken(eq(tokenPath), any(), eq(HOST + PATH + passwordPath)))
                .thenAnswer((Answer<ResponseDto>) invocation -> {
                    String providedToken = invocation.getArgument(1);
                    if (providedToken.equals(correctToken)) {
                        credVerStorage.isTokenCorrect = true;
                    }
                    if (credVerStorage.isNikCorrect && credVerStorage.isPassCorrect && credVerStorage.isTokenCorrect) {
                        return responseOf(dashboard,HOST + PATH + tokenPath);
                    } else {
                        return responseOf(invalidCrud,HOST + PATH + tokenPath);
                    }
                });

        lenient().when(connectionHandler.GETLogout(logoutPath,HOST + PATH + tokenPath))
                .thenReturn(responseOf("logout",HOST + DASHBOARD_PATH + logoutPath));

        lenient().when(connectionHandler.GETProductsPage(productsPath,HOST + PATH + tokenPath))
                .thenReturn(responseOf(products,HOST + DASHBOARD_PATH + productsPath));

        return connectionHandler;
    }

    private static ResponseDto responseOf(String html, String requestUrl) {
        ResponseDto.ResponseDtoBuilder builder = ResponseDto.builder();
        builder.setResponseBody(html);
        builder.setResponseHeaders(new HashMap<>());
        builder.setRequestUrl(requestUrl);
        builder.setStatus(200);
        return builder.build();
    }

    private static String testDataSupplier(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}