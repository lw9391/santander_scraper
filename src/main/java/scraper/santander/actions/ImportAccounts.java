package scraper.santander.actions;

import org.jsoup.nodes.Document;
import scraper.santander.Account;

import java.util.List;

import static scraper.santander.actions.ResponseParser.extractAccounts;

public class ImportAccounts {

  private final SantanderHttpApi session;
  private final String productsPath;

  ImportAccounts(SantanderHttpApi session, String productsPath) {
    this.session = session;
    this.productsPath = productsPath;
  }

  public List<Account> run() {
    Document productsPage = session.productsPage(productsPath);
    List<Account> accountsDetails = extractAccounts(productsPage);
    session.logout();
    return accountsDetails;
  }

}
