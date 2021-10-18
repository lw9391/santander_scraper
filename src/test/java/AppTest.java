import scraper.App;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class AppTest {

  public static void main(String[] args) throws IOException {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalSystemOut = System.out;
    String[] credentials = readCredentials();

    System.setOut(new PrintStream(outContent));
    App.main(credentials[0], credentials[1]);
    String gatheredOutput = outContent.toString();
    System.setOut(originalSystemOut);
    outContent.close();

    String balanceRegex = "([0-9]{1,3}\\s)*[0-9]{1,3}\\,[0-9]{2}";
    Pattern pattern = Pattern.compile(balanceRegex);
    Matcher matcher = pattern.matcher(gatheredOutput);
    boolean containsAnyAmount = matcher.find();

    assertTrue(containsAnyAmount);
  }

  private static String[] readCredentials() {
    Scanner credentialsFileScanner = initFileScanner();
    String nik = credentialsFileScanner.nextLine();
    String password = credentialsFileScanner.nextLine();
    credentialsFileScanner.close();
    return new String[]{nik, password};
  }

  private static Scanner initFileScanner() {
    try {
      File credentialsFile = new File("credentials.txt");
      return new Scanner(credentialsFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File with credentials not found.");
    }
  }
}