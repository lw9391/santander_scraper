package scraper.santander;

import scraper.InvalidCredentialsException;

public class Credentials {
  public final String accountNumber;
  public final String password;

  public Credentials(String accountNumber, String password) {
    if (!verifyAccountNumber(accountNumber)) {
      throw new InvalidCredentialsException("Nik must be between 6 and 20 characters.");
    }
    if (!verifyPassword(password)) {
      throw new InvalidCredentialsException("Password must be between 4 and 20 characters.");
    }
    this.accountNumber = accountNumber;
    this.password = password;
  }

  private boolean verifyAccountNumber(String accountNumber) {
    boolean minLength = accountNumber.length() >= 6;
    boolean maxLength = accountNumber.length() < 20;
    return minLength && maxLength;
  }

  private boolean verifyPassword(String password) {
    boolean minLength = password.length() >= 4;
    boolean maxLength = password.length() < 20;
    return minLength && maxLength;
  }
}
