package scraper.view;

import scraper.AccountDetails;

import java.util.List;

public interface ViewController {
  String readInput();

  void displayPromptForSmsCode();

  void displayOutput(List<AccountDetails> accountsList);
}
