package scraper.view;

import scraper.AccountDetails;

import java.util.List;
import java.util.Scanner;

public class ConsoleController implements ViewController {

  private static final String PROMPT_FOR_SMS_CODE = "Wprowadz sms-kod:";

  @Override
  public String readInput() {
    Scanner in = new Scanner(System.in);
    String input = in.nextLine();
    in.close();
    return input;
  }

  @Override
  public void displayPromptForSmsCode() {
    System.out.println(PROMPT_FOR_SMS_CODE);
  }

  @Override
  public void displayOutput(List<AccountDetails> accountsList) {
    accountsList.forEach(System.out::println);
  }
}
