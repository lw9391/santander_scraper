package scraper;

import scraper.santander.Account;
import scraper.santander.View;

import java.util.List;
import java.util.Scanner;

public class Console implements View {

  private static final String PROMPT_FOR_SMS_CODE = "Wprowadz sms-kod:";

  @Override
  public String readSmsCode() {
    System.out.println(PROMPT_FOR_SMS_CODE);
    Scanner in = new Scanner(System.in);
    String input = in.nextLine();
    in.close();
    return input;
  }

  @Override
  public void display(List<Account> accountsList) {
    accountsList.stream()
            .map(account -> "Account name = " + account.accountName() + ", Balance = " + account.balance())
            .forEach(System.out::println);
  }

}
