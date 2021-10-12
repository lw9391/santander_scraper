package scraper.view;

import scraper.AccountDetails;

import java.util.List;

public interface ViewController {
  String readInput();

  void displayMessage(String message);

  void displayOutput(List<AccountDetails> accountsList);
}
