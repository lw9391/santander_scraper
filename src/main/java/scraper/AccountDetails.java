package scraper;

import java.util.Objects;

public class AccountDetails {
  private final String accountName;
  private final String balance;

  public AccountDetails(String accountName, String balance) {
    this.accountName = accountName;
    this.balance = balance;
  }

  public String getAccountName() {
    return accountName;
  }

  public String getBalance() {
    return balance;
  }

  @Override
  public String toString() {
    return "Account name = " + accountName + ", Balance = " + balance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AccountDetails that = (AccountDetails) o;
    return Objects.equals(accountName, that.accountName) && Objects.equals(balance, that.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountName, balance);
  }
}
