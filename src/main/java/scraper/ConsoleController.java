package scraper;

import java.util.List;
import java.util.Scanner;

public class ConsoleController implements ViewController{
    @Override
    public String readInput() {
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        in.close();
        return input;
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayOutput(List<AccountDetails> accountsList) {
        accountsList.forEach(System.out::println);
    }
}
