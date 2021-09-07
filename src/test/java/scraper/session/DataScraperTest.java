package scraper.session;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scraper.AccountDetails;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataScraperTest {
    private static DataScraper scraper;

    @BeforeAll
    static void beforeAll() {
        scraper = new DataScraper();
    }

    @Test
    void scrapXmlPathFromLoginPage() {
        String logPage = testDataSupplier("src/test/resources/http/1logPage.html");
        String scrapedPath = scraper.scrapXmlPathFromLoginPage(logPage);
        assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2FI3qkyM18bV", scrapedPath);
    }

    @Test
    void scrapNikPagePathFromRedirectXml() {
        String xml = testDataSupplier("src/test/resources/http/2redirectXml.xml");
        String scrapedPath = scraper.scrapNikPagePathFromRedirectXml(xml);
        assertEquals("/login?x=psSMC6gVvVpYkVO8biaMI7tUqDDpYQzXOM_jr6v8ttKTh5E-e7iMgxJxSTtwaxrIe8mQhG9jUN5lx1Yyr-wI2Jq7iUgK71WU9KRSiD9ZXtSc6N1yJH61vg", scrapedPath);
    }

    @Test
    void scrapPasswordPagePathFromNikResponse() {
        String page = testDataSupplier("src/test/resources/http/3redirectXml.xml");
        String scrapedPath = scraper.scrapPasswordPagePathFromNikResponse(page);
        assertEquals("/crypt.brKnpZUkktuD2YnBIm0vpQ/brK0a", scrapedPath);
    }

    @Test
    void scrapPathsFromPasswordPage() {
        String page = testDataSupplier("src/test/resources/http/4loginpage.html");
        Map<PathsNames,String> scrapedPaths = scraper.scrapPathsFromPasswordPage(page);
        assertEquals("/crypt.brKnpZUkktsTyMD4fDym_SLk_R9DvRZrI8wCGgwoOlCfiXbbYM9ZJhVOk0kArlJ9bSYrrEyANi1n2ESVzY5GrffYXOGcjl9xFRMTUc2Ufq8/brK0a", scrapedPaths.get(PathsNames.PASSWORD));
        assertEquals("/crypt.brKnpZUkktsTyMD4fDym_YLJ6XzBNKJtQSbN-NdTTUaXMzfLzxBZ9EURsRnaBBQxR_jFThmXQm0zbzjNSjxOtMufJ-0MGGRcS6TA4seUNnspto52VanATw/brK0a", scrapedPaths.get(PathsNames.SESSION_MAP));
    }

    @Test
    void scrapTokenPathFromPasswordResponse() {
        String page = testDataSupplier("src/test/resources/http/5tokenpage.html");
        String scrapedPath = scraper.scrapTokenPathFromPasswordResponse(page);
        assertEquals("/crypt.brKnpZUkktvUK1iu4qXMi9bnZ5hezbPacPk819Dz6-8g_orQ4Xq-FjTWUHuDABm_P42aHIYvzffjV0KTJBs5ldLgqxB1y_j3MyHdo2lsyqlmW45BWuI_jcLCy__ihsl4/brK0a", scrapedPath);
    }

    @Test
    void scrapeInvalidLoginDiv() {
        String invalidCrudPage = testDataSupplier("src/test/resources/http/8invalidCrudPage.html");
        String invalidLoginInfo = scraper.scrapeInvalidLoginDiv(invalidCrudPage);
        boolean expectedTrue = invalidLoginInfo.contains("wylogowanie");
        assertTrue(expectedTrue);

        String dashboard = testDataSupplier("src/test/resources/http/6dashboard.html");
        String expectedEmpty = scraper.scrapeInvalidLoginDiv(dashboard);
        assertTrue(expectedEmpty.isEmpty());
    }

    @Test
    void scrapPathsFromDashboardPage() {
        String page = testDataSupplier("src/test/resources/http/6dashboard.html");
        Map<PathsNames,String> scrapedPaths = scraper.scrapPathsFromDashboardPage(page);
        assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jESpGENeIF4xRXVg0UDT17jg", scrapedPaths.get(PathsNames.LOGOUT));
        assertEquals("/dashboard?x=dhkGTXuV40VOHTFeXCsiKQwa_Jf2z0jEA84g18nLiMe2NjQEn9CgFQ9xGEI9imD2CH07NF_4-1SHx_N-xlO3J6tWfhjyQ0YhzvUgX_37trHGKjggK4JpehiAzGO9SxQrE1fghwvJtv5JhxKwamTKQYMQ0ZoNYzV8EmMYKU9r_Zo", scrapedPaths.get(PathsNames.PRODUCTS));
    }

    @Test
    void scrapAccountsInformationFromProductsPage() {
        String page = testDataSupplier("src/test/resources/http/7products.html");
        List<AccountDetails> accountDetails = scraper.scrapAccountsInformationFromProductsPage(page);
        assertEquals(2, accountDetails.size());
    }

    private String testDataSupplier(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}