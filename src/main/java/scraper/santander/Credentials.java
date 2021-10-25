package scraper.santander;

public record Credentials(String accountNumber, String password) {

  public Credentials {
    assertAccountNumberHasProperLength(accountNumber);
    assertPasswordHasProperLength(password);
  }

  private static void assertAccountNumberHasProperLength(String accountNumber) {
    if (accountNumber.length() < 6 || accountNumber.length() > 20)
      throw new InvalidCredentialsException("Nik must be between 6 and 20 characters.");
  }

  private static void assertPasswordHasProperLength(String password) {
    if (password.length() < 4 || password.length() > 20)
      throw new InvalidCredentialsException("Password must be between 4 and 20 characters.");
  }

}
