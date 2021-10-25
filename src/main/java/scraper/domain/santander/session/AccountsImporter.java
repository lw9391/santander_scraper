package scraper.domain.santander.session;

import scraper.domain.AccountDetails;
import scraper.domain.http.Response;

import java.util.List;

import static scraper.domain.santander.session.HttpResponseParser.extractAccountsInformationFromProductsPage;

public class AccountsImporter {

  private final HttpExchanges session;
  private final String productsPath;

  AccountsImporter(HttpExchanges session, String productsPath) {
    this.session = session;
    this.productsPath = productsPath;
  }

  public List<AccountDetails> importAccounts() {
    Response response = session.productsPage(productsPath);
    List<AccountDetails> accountsDetails = extractAccountsInformationFromProductsPage(response.body);
    session.logout();
    return accountsDetails;
  }

}
