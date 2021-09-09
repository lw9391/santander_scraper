package scraper;

public class Credentials {
    private final String accountNumber;
    private final String password;

    public Credentials(String accountNumber, String password) {
        this.accountNumber = accountNumber;
        this.password = password;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPassword() {
        return password;
    }
}
