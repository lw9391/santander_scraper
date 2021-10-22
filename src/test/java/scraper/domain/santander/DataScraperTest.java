package scraper.domain.santander;

import org.junit.jupiter.api.Test;
import scraper.domain.AccountDetails;
import scraper.domain.santander.session.DataScraper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataScraperTest {

  @Test
  void scrapeXmlPathFromLoginPage() {
    String logPage = testDataSupplier("src/test/resources/http/1logPage.html");
    String scrapedPath = DataScraper.scrapeXmlPathFromLoginPage(logPage);
    assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2FI3qkyM18bV", scrapedPath);
  }

  @Test
  void scrapeNikPagePathFromRedirectXml() {
    String xml = testDataSupplier("src/test/resources/http/2redirectXml.xml");
    String scrapedPath = DataScraper.scrapeNikPagePathFromRedirectXml(xml);
    assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2Jq7iUgK71WU9KRSiD9ZXtSc6N1yJH61vg", scrapedPath);
  }

  @Test
  void scrapePasswordPagePathFromNikResponse() {
    String page = testDataSupplier("src/test/resources/http/3redirectXml.xml");
    String scrapedPath = DataScraper.scrapePasswordPagePathFromNikResponse(page);
    assertEquals("/crypt.brKnpZUkktuD2YnBIm0vpQ/brK0a", scrapedPath);
  }

  @Test
  void scrapePathsFromPasswordPage() {
    String page = testDataSupplier("src/test/resources/http/4loginpage.html");
    String scrapedPath = DataScraper.scrapePasswordPathFromPasswordPage(page);
    assertEquals("/crypt.brKnpZUkktsTyMD4fDym_SLk_R9DvRZrI8wCGgwoOlCfiXbbYM9ZJhVOk0kArlJ9bSYrrEyANi1n2ESVzY5GrffYXOGcjl9xFRMTUc2Ufq8/brK0a", scrapedPath);
  }

  @Test
  void scrapeTokenPathFromPasswordResponse() {
    String page = testDataSupplier("src/test/resources/http/5tokenpage.html");
    String scrapedPath = DataScraper.scrapeSmsCodePathFromPasswordResponse(page);
    assertEquals("/crypt.brKnpZUkktvUK1iu4qXMi9bnZ5hezbPacPk819Dz6-8g_orQ4Xq-FjTWUHuDABm_P42aHIYvzffjV0KTJBs5ldLgqxB1y_j3MyHdo2lsyqlmW45BWuI_jcLCy__ihsl4/brK0a", scrapedPath);
  }

  @Test
  void scrapeLogoutButton() {
    String dashboard = testDataSupplier("src/test/resources/http/6dashboard.html");
    boolean hasLogoutButton = DataScraper.hasLogoutButton(dashboard);

    assertTrue(hasLogoutButton);
  }

  @Test
  void scrapeLogoutButtonOnInvalidCrudPageExpectEmpty() {
    String invalidCrudPage = testDataSupplier("src/test/resources/http/8invalidCrudPage.html");
    boolean hasLogoutButton = DataScraper.hasLogoutButton(invalidCrudPage);
    assertFalse(hasLogoutButton);
  }

  @Test
  void scrapePathsFromDashboardPage() {
    String page = testDataSupplier("src/test/resources/http/6dashboard.html");
    String scrapedPath = DataScraper.scrapeProductsPathFromDashboardPage(page);
    assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jEA84g18nLiMe2NjQEn9CgFQ9xGEI9imD2CH07NF_4-1SHx_N-xlO3J6tWfhjyQ0YhzvUgX_37trHGKjggK4JpehiAzGO9SxQrE1fghwvJtv5JhxKwamTKQYMQ0ZoNYzV8EmMYKU9r_Zo", scrapedPath);
  }

  @Test
  void scrapeAccountsInformationFromProductsPage() {
    String page = testDataSupplier("src/test/resources/http/7products.html");
    List<AccountDetails> accountDetails = DataScraper.scrapeAccountsInformationFromProductsPage(page);
    assertEquals(2, accountDetails.size());
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