package scraper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

  public static void main(String[] args) {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));

    App.main(readCredentials());

    assertTrue(containsAccountLine(outContent.toString()));
  }

  private static String[] readCredentials() {
    InputStream inputStream = AppTest.class.getResourceAsStream("credentials");
    Scanner scanner = new Scanner(inputStream);
    String nik = scanner.nextLine();
    String password = scanner.nextLine();
    return new String[]{nik, password};
  }

  private static boolean containsAccountLine(String gatheredOutput) {
    return gatheredOutput.lines().anyMatch(line -> {
      Pattern pattern = Pattern.compile("Account name = .+, Balance = .+");
      Matcher matcher = pattern.matcher(line);
      return matcher.matches();
    });
  }

}