package scraper.santander.actions;

import scraper.santander.AccountDetails;
import scraper.santander.http.Response;

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
    Response response = session.productsPage(productsPath);
    List<AccountDetails> accountsDetails = extractAccountsInformationFromProductsPage(response.body);
    session.logout();
    return accountsDetails;
  }

}
