package scraper;

public record AccountDetails(String accountName, String balance) {

  @Override
  public String toString() {
    return "Account name = " + accountName + ", Balance = " + balance;
  }

}
