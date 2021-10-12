package scraper;

public interface CredentialsVerifier {
  boolean verifyAccountNumber(String accountNumber);

  boolean verifyPassword(String password);

  boolean verifyToken(String token);
}
