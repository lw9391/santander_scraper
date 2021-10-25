package scraper.santander.actions;

import org.jsoup.nodes.Document;
import scraper.santander.AccountDetails;

import java.util.List;

import static scraper.santander.actions.HttpResponseParser.extractAccountsInformationFromProductsPage;

public class ImportAccounts {

  private final SantanderHttpApi session;
  private final String productsPath;

  ImportAccounts(SantanderHttpApi session, String productsPath) {
    this.session = session;
    this.productsPath = productsPath;
  }

  public List<AccountDetails> run() {
    Document response = session.productsPage(productsPath);
    List<AccountDetails> accountsDetails = extractAccountsInformationFromProductsPage(response);
    session.logout();
    return accountsDetails;
  }

}
