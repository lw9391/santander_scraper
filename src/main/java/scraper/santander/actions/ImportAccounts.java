package scraper.santander.actions;

import org.jsoup.nodes.Document;
import scraper.santander.AccountDetails;

import java.util.List;

import static scraper.santander.actions.ResponseParser.extractAccountsInformation;

public class ImportAccounts {

  private final SantanderHttpApi session;
  private final String productsPath;

  ImportAccounts(SantanderHttpApi session, String productsPath) {
    this.session = session;
    this.productsPath = productsPath;
  }

  public List<AccountDetails> run() {
    Document productsPage = session.productsPage(productsPath);
    List<AccountDetails> accountsDetails = extractAccountsInformation(productsPage);
    session.logout();
    return accountsDetails;
  }

}
