package scraper.santander.santander;

import scraper.santander.Account;
import scraper.santander.View;

import java.util.ArrayList;
import java.util.List;

public class ViewStub implements View {

  private final List<Account> storedDetails;

  public ViewStub() {
    storedDetails = new ArrayList<>();
  }

  @Override
  public String readSmsCode() {
    return "111-111";
  }

  @Override
  public void display(List<Account> accountsList) {
    storedDetails.addAll(accountsList);
  }

  public List<Account> getStoredDetails() {
    return storedDetails;
  }

}
