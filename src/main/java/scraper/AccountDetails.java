package scraper;

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
}
