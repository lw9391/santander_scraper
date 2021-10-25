package scraper.santander.actions;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scraper.santander.Account;

import java.util.List;
import java.util.stream.Collectors;

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

  private static String extractFormAction(Document passwordPage, String pinForm) {
    String attribute = passwordPage.getElementById(pinForm)
            .attr("action");
    int startIndex = attribute.indexOf("/crypt.");
    return attribute.substring(startIndex);
  }

  public static boolean hasLogoutButton(Document smsCodeResponse) {
    Element logout = smsCodeResponse.getElementsByClass("logout").first();
    return logout != null;
  }

  public static String extractProductsPath(Document dashboardPage) {
    return dashboardPage.getElementById("menu_all_products")
            .select("a")
            .attr("href")
            .substring(1);
  }

  public static List<Account> extractAccounts(Document productsPage) {
    return productsPage.getElementById("avistaAccountsBoxContent")
            .selectFirst("table")
            .select("tbody tr")
            .stream()
            .map(ResponseParser::extractAccount)
            .collect(Collectors.toList());
  }

  private static Account extractAccount(Element row) {
    String accountName = row.getElementsByClass("name")
            .select("div a")
            .text();
    String balanceCurrency = row.getElementsByClass("money")
            .first()
            .select("div")
            .text()
            .replaceAll("&nbsp;", " ");
    return new Account(accountName, balanceCurrency);
  }

}
