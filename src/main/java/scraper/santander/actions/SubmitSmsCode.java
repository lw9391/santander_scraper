package scraper.santander.actions;

import org.jsoup.nodes.Document;
import scraper.santander.InvalidCredentialsException;

import static scraper.santander.actions.ResponseParser.extractProductsPath;
import static scraper.santander.actions.ResponseParser.hasLogoutButton;

public class SubmitSmsCode {

  private final SantanderHttpApi exchanges;
  private final String smsCodeConfirmationPath;

  SubmitSmsCode(SantanderHttpApi session, String smsCodeConfirmationPath) {
    exchanges = session;
    this.smsCodeConfirmationPath = smsCodeConfirmationPath;
  }

  public ImportAccounts run(String smsCode) {
    Document dashboard = exchanges.smsCode(smsCodeConfirmationPath, smsCode);
    if (!hasLogoutButton(dashboard))
      throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
    String productsPath = extractProductsPath(dashboard);
    return new ImportAccounts(exchanges, productsPath);
  }

}
