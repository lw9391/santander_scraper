import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scraper.App;
import scraper.santander.Credentials;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static scraper.santander.SantanderAccountsScraper.PROMPT_FOR_SMS_CODE;

public class AcceptanceTest {
  private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private static final PrintStream originalOut = System.out;
  private static final Credentials credentials = readCredentials();

  @BeforeEach
  void setUpStream() {
    System.setOut(new PrintStream(outContent));
  }

  @Test
  void testAppCheckIfScrapedAnyInformation() {
    App.main(credentials.accountNumber, credentials.password);
    String scrapedAccountsData = originalOut.toString();
    assertTrue(scrapedAccountsData.length() > PROMPT_FOR_SMS_CODE.length());
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

  @AfterEach
  void restoreStream() {
    System.setOut(originalOut);
  }

  @AfterAll
  static void closeStream() throws IOException {
    outContent.close();
  }
}