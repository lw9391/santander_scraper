package scraper.santander;

import scraper.CredentialsVerifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SantanderCredentialsVerifier implements CredentialsVerifier {
    @Override
    public boolean verifyAccountNumber(String accountNumber) {
        boolean minLength = accountNumber.length() >= 6;
        boolean maxLength = accountNumber.length() < 20;
        return minLength && maxLength;
    }

    @Override
    public boolean verifyPassword(String password) {
        boolean minLength = password.length() >= 4;
        boolean maxLength = password.length() < 20;
        return minLength && maxLength;
    }

    @Override
    public boolean verifyToken(String token) {
        String regex = "[0-9]{3}\\-[0-9]{3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(token);
        return matcher.matches();
    }
}
