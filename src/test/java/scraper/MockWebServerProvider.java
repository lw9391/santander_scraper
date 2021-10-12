package scraper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MockWebServerProvider {
  public static final String PATH = "/centrum24-web";
  public static final String LOGOUT = "/centrum24-web/logout";

  public static MockWebServer getServer() {
    MockWebServer server = new MockWebServer();

    server.enqueue(loginRedirect());
    server.enqueue(loginPage());
    server.enqueue(xmlWithPathForNikPage());
    server.enqueue(postNikResponse());
    server.enqueue(passwordPage());
    server.enqueue(sessionMapResponse());
    server.enqueue(tokenPage());
    server.enqueue(dashboardPage());
    server.enqueue(productsPage());
    server.enqueue(logoutRedirect());
    server.enqueue(logoutPage());

    return server;
  }

  public static MockWebServer getServerWithResponsesForInvalidCred() {
    MockWebServer server = new MockWebServer();

    server.enqueue(loginRedirect());
    server.enqueue(loginPage());
    server.enqueue(xmlWithPathForNikPage());
    server.enqueue(postNikResponse());
    server.enqueue(passwordPage());
    server.enqueue(sessionMapResponse());
    server.enqueue(tokenPage());
    server.enqueue(invalidLoginPage());

    return server;
  }

  private static MockResponse loginRedirect() {
    return new MockResponse()
            .setResponseCode(302)
            .setHeader("Location", PATH + "/login?x=vJL0iClolC8");
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

  private static MockResponse logoutRedirect() {
    return new MockResponse()
            .setResponseCode(302)
            .setHeader("Location", LOGOUT);
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