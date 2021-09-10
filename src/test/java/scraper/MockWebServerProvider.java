package scraper;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockWebServerProvider {
    public static final String HOST = "http://127.0.0.1:8889";
    public static final String PATH = "/centrum24-web";
    public static final String DASHBOARD_PATH = "/centrum24-web/multi";
    public static final String LOGOUT = HOST + "/centrum24-web/logout";

    private static final String xmlPath = "/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2FI3qkyM18bV";
    private static final String nikPath = "/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2Jq7iUgK71WU9KRSiD9ZXtSc6N1yJH61vg";
    private static final String passPagePath = "/crypt.brKnpZUkktuD2YnBIm0vpQ/brK0a";
    private static final String sessionMapPath = "/crypt.brKnpZUkktsTyMD4fDym_YLJ6XzBNKJtQSbN-NdTTUaXMzfLzxBZ9EURsRnaBBQxR_jFThmXQm0zbzjNSjxOtMufJ-0MGGRcS6TA4seUNnspto52VanATw/brK0a";
    private static final String passwordPath = "/crypt.brKnpZUkktsTyMD4fDym_SLk_R9DvRZrI8wCGgwoOlCfiXbbYM9ZJhVOk0kArlJ9bSYrrEyANi1n2ESVzY5GrffYXOGcjl9xFRMTUc2Ufq8/brK0a";
    private static final String tokenPath = "/crypt.brKnpZUkktvUK1iu4qXMi9bnZ5hezbPacPk819Dz6-8g_orQ4Xq-FjTWUHuDABm_P42aHIYvzffjV0KTJBs5ldLgqxB1y_j3MyHdo2lsyqlmW45BWuI_jcLCy__ihsl4/brK0a";
    private static final String productsPath = "/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jEA84g18nLiMe2NjQEn9CgFQ9xGEI9imD2CH07NF_4-1SHx_N-xlO3J6tWfhjyQ0YhzvUgX_37trHGKjggK4JpehiAzGO9SxQrE1fghwvJtv5JhxKwamTKQYMQ0ZoNYzV8EmMYKU9r_Zo";
    private static final String logoutPath = "/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jESpGENeIF4xRXVg0UDT17jg";
    private static final String sessionMap = "true%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Ctrue%2Cfalse%2C1300%2C1.5%2C1300%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Ctrue%2Cfalse%2Cfalse";

    public static MockWebServer getMockWebServer(Credentials credentials, String token) {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(initDispatcher(credentials, token));
        return mockWebServer;
    }

    private static Dispatcher initDispatcher(Credentials credentials, String correctToken) {
        final var credVerStorage = new Object() {
            boolean isNikCorrect = false;
            boolean isPassCorrect = false;
            boolean isTokenCorrect = false;
        };

        return new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                String url = recordedRequest.getRequestUrl().toString();
                String body = recordedRequest.getBody().readUtf8();

                switch (url) {
                    /* login page request */
                    case HOST + PATH + "/login" -> {
                        return loginRedirect();
                    }

                    /* redirect from login page */
                    case HOST + PATH + "/login?x=vJL0iClolC8" -> {
                        return loginPage();
                    }

                    /* post request with nik */
                    case HOST + PATH + nikPath -> {
                        if (nikBodyCheck(body)) {
                            int start = 4;
                            int end = body.indexOf("&dp");
                            String nik = body.substring(start, end);
                            if (nik.equals(credentials.getAccountNumber())) {
                                credVerStorage.isNikCorrect = true;
                            }
                            return postNikResponse();
                        } else
                            return loginPage();
                    }

                    /* request for page with password form */
                    case HOST + PATH + passPagePath -> {
                        return passwordPage();
                    }

                    /* post request with password */
                    case HOST + PATH + passwordPath -> {
                        if (passBodyCheck(body)) {
                            int start = body.indexOf("=") + 1;
                            int end = body.indexOf("&loginButton");
                            String password = body.substring(start, end);
                            if (password.equals(credentials.getPassword())) {
                                credVerStorage.isPassCorrect = true;
                            }
                        }
                        return tokenPage();
                    }

                    /* post request with token */
                    case HOST + PATH + tokenPath -> {
                        if (tokenBodyCheck(body)) {
                            int start = body.indexOf("=") + 1;
                            int end = body.indexOf("&loginButton");
                            String token = body.substring(start, end);
                            if (token.equals(correctToken)) {
                                credVerStorage.isTokenCorrect = true;
                            }
                        }
                        if (credVerStorage.isNikCorrect && credVerStorage.isPassCorrect && credVerStorage.isTokenCorrect) {
                            return dashboardPage();
                        } else return invalidLoginPage();
                    }

                    /* products page request */
                    case HOST + DASHBOARD_PATH + productsPath -> {
                        return productsPage();
                    }

                    case HOST + DASHBOARD_PATH + logoutPath, LOGOUT -> {
                        return logoutPage();
                    }

                }

                if (matchesXmlRequestPath(url)) {
                    return xmlWithPathForNikPage();
                }

                if (matchesSessionMapPath(url)) {
                    return sessionMapResponse();
                }

                return loginRedirect();
            }
        };
    }

    private static boolean nikBodyCheck(String nikBody) {
        String regex = "nik=.{6,20}?&dp=.*?&loginButton=1";
        return matches(regex, nikBody);
    }

    private static boolean passBodyCheck(String passBody) {
        String regex = "pinFragment%3Apin=.{4,20}&loginButton=Dalej";
        return matches(regex, passBody);
    }

    private static boolean tokenBodyCheck(String tokenBody) {
        String regex = "response=\\d{3}\\-\\d{3}&loginButton=Dalej";
        return matches(regex, tokenBody);
    }

    private static boolean matchesXmlRequestPath(String url) {
        String regex = HOST + PATH + xmlPath + "&_=\\d+";
        regex = regex.replaceAll("/","\\/")
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\?","\\\\?");
        return matches(regex, url);
    }

    private static boolean matchesSessionMapPath(String url) {
        String regex = HOST + PATH + sessionMapPath + "?sessionMap=" + sessionMap + "&_=\\d+";
        regex = regex.replaceAll("/","\\/")
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\?","\\\\?");
        return matches(regex, url);
    }

    private static boolean matches(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private static MockResponse loginRedirect() {
        return new MockResponse()
                .setResponseCode(302)
                .setHeader("Location", HOST + PATH + "/login?x=vJL0iClolC8");
    }

    private static MockResponse loginPage() {
        String body = getResponseBody("src/test/resources/http/1logPage.html");
        return new MockResponse()
                .setBody(body)
                .setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private static MockResponse xmlWithPathForNikPage() {
        String body = getResponseBody("src/test/resources/http/2redirectXml.xml");
        return new MockResponse()
                .setBody(body)
                .setHeader("Content-Type", "text/xml; charset=UTF-8");
    }

    private static MockResponse postNikResponse() {
        String body = getResponseBody("src/test/resources/http/3redirectXml.xml");
        return new MockResponse()
                .setBody(body)
                .setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private static MockResponse passwordPage() {
        String body = getResponseBody("src/test/resources/http/4loginpage.html");
        return new MockResponse()
                .setBody(body)
                .setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private static MockResponse sessionMapResponse() {
        return new MockResponse()
                .setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ajax-response></ajax-response>")
                .setHeader("Content-Type", "text/xml; charset=UTF-8");
    }

    private static MockResponse tokenPage() {
        String body = getResponseBody("src/test/resources/http/5tokenpage.html");
        return new MockResponse()
                .setBody(body)
                .setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private static MockResponse dashboardPage() {
        String body = getResponseBody("src/test/resources/http/6dashboard.html");
        return new MockResponse()
                .setBody(body)
                .setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private static MockResponse logoutPage() {
        return new MockResponse()
                .setBody("Poprawnie wylogowany")
                .setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private static MockResponse productsPage() {
        String body = getResponseBody("src/test/resources/http/7products.html");
        return new MockResponse()
                .setBody(body)
                .setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private static MockResponse invalidLoginPage() {
        String body = getResponseBody("src/test/resources/http/8invalidCrudPage.html");
        return new MockResponse()
                .setBody(body)
                .setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private static String getResponseBody(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}