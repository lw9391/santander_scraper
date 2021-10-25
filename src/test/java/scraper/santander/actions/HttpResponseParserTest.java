package scraper.santander.actions;

import org.junit.jupiter.api.Test;
import scraper.santander.AccountDetails;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseParserTest {

  @Test
  void scrapeXmlPathFromLoginPage() {
    String logPage = testDataSupplier("src/test/resources/http/1logPage.html");
    String scrapedPath = HttpResponseParser.extractXmlPathFromLoginPage(logPage);
    assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2FI3qkyM18bV", scrapedPath);
  }

  @Test
  void scrapeNikPagePathFromRedirectXml() {
    String xml = testDataSupplier("src/test/resources/http/2redirectXml.xml");
    String scrapedPath = HttpResponseParser.extractNikPagePathFromRedirectXml(xml);
    assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2Jq7iUgK71WU9KRSiD9ZXtSc6N1yJH61vg", scrapedPath);
  }

  @Test
  void scrapePasswordPagePathFromNikResponse() {
    String page = testDataSupplier("src/test/resources/http/3redirectXml.xml");
    String scrapedPath = HttpResponseParser.extractPasswordPagePathFromNikResponse(page);
    assertEquals("/crypt.brKnpZUkktuD2YnBIm0vpQ/brK0a", scrapedPath);
  }

  @Test
  void scrapePathsFromPasswordPage() {
    String page = testDataSupplier("src/test/resources/http/4loginpage.html");
    String scrapedPath = HttpResponseParser.extractPasswordPathFromPasswordPage(page);
    assertEquals("/crypt.brKnpZUkktsTyMD4fDym_SLk_R9DvRZrI8wCGgwoOlCfiXbbYM9ZJhVOk0kArlJ9bSYrrEyANi1n2ESVzY5GrffYXOGcjl9xFRMTUc2Ufq8/brK0a", scrapedPath);
  }

  @Test
  void scrapeTokenPathFromPasswordResponse() {
    String page = testDataSupplier("src/test/resources/http/5tokenpage.html");
    String scrapedPath = HttpResponseParser.extractSmsCodePathFromPasswordResponse(page);
    assertEquals("/crypt.brKnpZUkktvUK1iu4qXMi9bnZ5hezbPacPk819Dz6-8g_orQ4Xq-FjTWUHuDABm_P42aHIYvzffjV0KTJBs5ldLgqxB1y_j3MyHdo2lsyqlmW45BWuI_jcLCy__ihsl4/brK0a", scrapedPath);
  }

  @Test
  void scrapeLogoutButton() {
    String dashboard = testDataSupplier("src/test/resources/http/6dashboard.html");
    boolean hasLogoutButton = HttpResponseParser.hasLogoutButton(dashboard);

    assertTrue(hasLogoutButton);
  }

  @Test
  void scrapeLogoutButtonOnInvalidCrudPageExpectEmpty() {
    String invalidCrudPage = testDataSupplier("src/test/resources/http/8invalidCrudPage.html");
    boolean hasLogoutButton = HttpResponseParser.hasLogoutButton(invalidCrudPage);
    assertFalse(hasLogoutButton);
  }

  @Test
  void scrapePathsFromDashboardPage() {
    String page = testDataSupplier("src/test/resources/http/6dashboard.html");
    String scrapedPath = HttpResponseParser.extractProductsPathFromDashboardPage(page);
    assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jEA84g18nLiMe2NjQEn9CgFQ9xGEI9imD2CH07NF_4-1SHx_N-xlO3J6tWfhjyQ0YhzvUgX_37trHGKjggK4JpehiAzGO9SxQrE1fghwvJtv5JhxKwamTKQYMQ0ZoNYzV8EmMYKU9r_Zo", scrapedPath);
  }

  @Test
  void scrapeAccountsInformationFromProductsPage() {
    String page = testDataSupplier("src/test/resources/http/7products.html");
    List<AccountDetails> accountDetails = HttpResponseParser.extractAccountsInformationFromProductsPage(page);
    assertEquals("112,00 PLN", accountDetails.get(0).balance());
    assertEquals("Ekstrakonto Plus", accountDetails.get(0).accountName());
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