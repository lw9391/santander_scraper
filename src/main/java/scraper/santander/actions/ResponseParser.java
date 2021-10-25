package scraper.santander.actions;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scraper.santander.AccountDetails;

import java.util.ArrayList;
import java.util.List;

public class ResponseParser {

  public static String extractXmlPath(Document loginPage) {
    String scriptWithQuery = loginPage.getElementById("DpsBtnEnable").html();
    return stringBetween(scriptWithQuery, "{\"u\":\".", "\",\"");
  }

  public static String extractNikPagePath(Document redirectXml) {
    String jsFunction = redirectXml.select("evaluate").html();
    return stringBetween(jsFunction, "{\"u\":\".", "\",\"");
  }

  public static String extractPasswordPagePath(Document nikResponsePage) {
    String redirectElement = nikResponsePage.select("redirect").html();
    return stringBetween(redirectElement, "CDATA[.", "]");
  }

  public static String extractPasswordPath(Document passwordPage) {
    return extractFormAction(passwordPage, "pinForm");
  }

  private static String stringBetween(String input, String start, String end) {
    int startIndex = input.indexOf(start);
    String part = input.substring(startIndex + start.length());
    int endIndex = part.indexOf(end);
    return part.substring(0, endIndex);
  }

  public static String extractSmsCodePath(Document passwordResponsePage) {
    return extractFormAction(passwordResponsePage, "authenticationForm");
  }

  public static boolean hasLogoutButton(Document smsCodeResponse) {
    Element logout = smsCodeResponse.getElementsByClass("logout").first();
    return logout != null;
  }

  @NotNull
  private static String extractFormAction(Document passwordPage, String pinForm) {
    String attribute = passwordPage.getElementById(pinForm)
            .attr("action");
    int startIndex = attribute.indexOf("/crypt.");
    return attribute.substring(startIndex);
  }

  public static String extractProductsPath(Document dashboardPage) {
    String productsLi = dashboardPage.getElementById("menu_all_products")
            .html();
    return Jsoup.parse(productsLi)
            .select("a")
            .attr("href")
            .substring(1);
  }

  public static List<AccountDetails> extractAccountsInformation(Document productsPage) {
    List<AccountDetails> personalAccounts = new ArrayList<>();
    Element table = productsPage.getElementById("avistaAccountsBoxContent")
            .selectFirst("table");
    if (table == null)
      return personalAccounts;
    personalAccounts.addAll(extractAccountsFromTable(table));
    return personalAccounts;
  }

  private static List<AccountDetails> extractAccountsFromTable(Element table) {
    List<AccountDetails> accountDetails = new ArrayList<>();
    Elements rows = table.select("tbody tr");
    for (Element row : rows) {
      String accountName = row.getElementsByClass("name")
              .select("div a")
              .text();
      String balanceCurrency = row.getElementsByClass("money")
              .first()
              .select("div")
              .text()
              .replaceAll("&nbsp;", " ");
      AccountDetails account = new AccountDetails(accountName, balanceCurrency);
      accountDetails.add(account);
    }
    return accountDetails;
  }

}
