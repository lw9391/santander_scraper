import scraper.App;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import static scraper.santander.SantanderAccountsScraper.PROMPT_FOR_SMS_CODE;

public class AcceptanceTest {

  public static void main(String[] args) {
    PrintStream outContent = new PrintStream(new ByteArrayOutputStream());
    PrintStream originalSystemOut = System.out;
    String[] credentials = readCredentials();

    System.setOut(outContent);
    App.main(credentials[0], credentials[1]);
    String gatheredOutput = outContent.toString();
    System.setOut(originalSystemOut);
    outContent.close();

    if (gatheredOutput.length() > PROMPT_FOR_SMS_CODE.length()) {
      System.out.println("Test passed");
    } else {
      System.out.println("Test failed");
    }
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