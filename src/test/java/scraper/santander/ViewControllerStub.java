package scraper.santander;

import scraper.AccountDetails;
import scraper.view.ViewController;

import java.util.ArrayList;
import java.util.List;

public class ViewControllerStub implements ViewController {

  private final List<AccountDetails> storedDetails;

  public ViewControllerStub() {
    storedDetails = new ArrayList<>();
  }

  @Override
  public String readInput() {
    return "111-111";
  }

  @Override
  public void displayPromptForSmsCode() {

  }

  @Override
  public void displayOutput(List<AccountDetails> accountsList) {
    storedDetails.addAll(accountsList);
  }

  public List<AccountDetails> getStoredDetails() {
    return storedDetails;
  }

}
