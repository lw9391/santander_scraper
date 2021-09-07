package scraper;

import java.util.List;

public interface ViewController {
    String readInput();
    void displayMessage(String message);
    void displayOutput(List<AccountDetails> accountsList);
}
