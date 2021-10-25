package scraper.santander.santander;

import scraper.santander.AccountDetails;
import scraper.santander.View;

import java.util.ArrayList;
import java.util.List;

public class ViewStub implements View {

  private final List<AccountDetails> storedDetails;

  public ViewStub() {
    storedDetails = new ArrayList<>();
  }

  @Override
  public String readSmsCode() {
    return "111-111";
  }

  @Override
  public void display(List<AccountDetails> accountsList) {
    storedDetails.addAll(accountsList);
  }

  public List<AccountDetails> getStoredDetails() {
    return storedDetails;
  }

}
