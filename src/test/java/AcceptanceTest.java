import scraper.App;
import scraper.santander.Credentials;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import static scraper.santander.SantanderAccountsScraper.PROMPT_FOR_SMS_CODE;

public class AcceptanceTest {
  private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private static final PrintStream originalOut = System.out;
  private static final Credentials credentials = readCredentials();

  public static void main(String[] args) throws IOException {
    System.setOut(new PrintStream(outContent));
    App.main(credentials.accountNumber, credentials.password);
    String scrapedAccountsData = outContent.toString();
    System.setOut(originalOut);
    outContent.close();
    if (scrapedAccountsData.length() > PROMPT_FOR_SMS_CODE.length()) {
      System.out.println("Test passed");
    } else {
      System.out.println("Test failed");
    }
  }

  private static Credentials readCredentials() {
    Scanner credentialsFileScanner = initFileScanner();
    String nik = credentialsFileScanner.nextLine();
    String password = credentialsFileScanner.nextLine();
    credentialsFileScanner.close();
    return new Credentials(nik, password);
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