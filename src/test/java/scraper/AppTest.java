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
    PrintStream originalSystemOut = System.out;
    String[] credentials = readCredentials();

    System.setOut(new PrintStream(outContent));
    App.main(credentials[0], credentials[1]);
    String gatheredOutput = outContent.toString();
    System.setOut(originalSystemOut);

    assertTrue(hasLinesInCorrectFormat(gatheredOutput));
  }

  private static String[] readCredentials() {
    InputStream inputStream = AppTest.class.getResourceAsStream("credentials");
    Scanner scanner = new Scanner(inputStream);
    String nik = scanner.nextLine();
    String password = scanner.nextLine();
    return new String[]{nik, password};
  }

  private static boolean hasLinesInCorrectFormat(String gatheredOutput) {
    String[] outputLines = gatheredOutput.split("//n");
    boolean areValid = true;
    String lineFormatRegex = "Account name = .+, Balance = .+";
    Pattern pattern = Pattern.compile(lineFormatRegex);
    for (int i = 1; i < outputLines.length; i++) {
      Matcher matcher = pattern.matcher(outputLines[i]);
      areValid = areValid && matcher.matches();
    }
    return areValid;
  }

}