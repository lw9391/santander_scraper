package scraper.domain.santander.session;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scraper.domain.AccountDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataScraper {

  public static String scrapeXmlPathFromLoginPage(String loginPageHtml) {
    String scriptWithQuery = getFromHtmlById(loginPageHtml, "DpsBtnEnable");
    String regexToFindJson = "\\{\"u\":\".*?\"\\}";
    String json = findInString(scriptWithQuery, regexToFindJson).get(0);
    return getJsonValue(json, "u").substring(1);
  }

  public static String scrapeNikPagePathFromRedirectXml(String redirectXml) {
    String jsFunction = getXmlElement(redirectXml, "evaluate");
    String regexToFindJson = "\\{\"u\":\".*?\\}";
    String json = findInString(jsFunction, regexToFindJson).get(0);
    return getJsonValue(json, "u").substring(1);
  }

  private static String getJsonValue(String json, String... fields) {
    JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
    for (int i = 0; i < fields.length - 1; i++) {
      jsonObject = jsonObject.get(fields[i]).getAsJsonObject();
    }
    return jsonObject.get(fields[fields.length - 1]).getAsString();
  }

  public static String scrapePasswordPagePathFromNikResponse(String nikResponsePageHtml) {
    String redirectElement = getXmlElement(nikResponsePageHtml, "redirect");
    return getSubstringBetween(redirectElement, "/", "]");
  }

  private static String getXmlElement(String xml, String tagName) {
    Document xmlDoc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());
    return xmlDoc.select(tagName).html();
  }

  public static String scrapePasswordPathFromPasswordPage(String passwordPageHtml) {
    String attribute = getAttributeFromHtml(passwordPageHtml, "pinForm", "action");
    int startIndex = attribute.indexOf("/crypt.");

    return attribute.substring(startIndex);
  }

  private static String getSubstringBetween(String input, String start, String end) {
    int startIndex = input.indexOf(start);
    String part = input.substring(startIndex);
    int endIndex = part.indexOf(end);
    return part.substring(0, endIndex);
  }

  public static String scrapeSmsCodePathFromPasswordResponse(String passwordResponsePageHtml) {
    String attribute = getAttributeFromHtml(passwordResponsePageHtml, "authenticationForm", "action");
    int startIndex = attribute.indexOf("/crypt.");
    return attribute.substring(startIndex);
  }

  private static String getAttributeFromHtml(String html, String id, String attribute) {
    return Jsoup.parse(html)
            .getElementById(id)
            .attr(attribute);
  }

  public static boolean hasLogoutButton(String smsCodeResponseHtml) {
    Element logout = Jsoup.parse(smsCodeResponseHtml).getElementsByClass("logout").first();
    return logout != null;
  }

  public static String scrapeProductsPathFromDashboardPage(String dashboardPageHtml) {
    String productsLi = getFromHtmlById(dashboardPageHtml, "menu_all_products");

    return Jsoup.parse(productsLi).select("a").attr("href").substring(1);
  }

  private static String getFromHtmlById(String html, String id) {
    return Jsoup.parse(html)
            .getElementById(id)
            .html();
  }

  public static List<AccountDetails> scrapeAccountsInformationFromProductsPage(String productsPageHtml) {
    return extractAccounts(productsPageHtml, "avistaAccountsBoxContent");
  }

  private static List<AccountDetails> extractAccounts(String html, String categoryId) {
    List<AccountDetails> personalAccounts = new ArrayList<>();
    Element table = Jsoup.parse(html)
            .getElementById(categoryId)
            .selectFirst("table");
    if (table == null)
      return personalAccounts;

    Elements rows = table.select("tbody")
            .select("tr");
    for (Element row : rows) {
      String accountName = row.getElementsByClass("name")
              .select("div")
              .select("a")
              .html();
      String balanceCurrency = row.getElementsByClass("money")
              .first()
              .select("div")
              .html()
              .replaceAll("&nbsp;", " ");
      AccountDetails accountDetail = new AccountDetails(accountName, balanceCurrency);
      personalAccounts.add(accountDetail);
    }
    return personalAccounts;
  }

  private static List<String> findInString(String input, String regex) {
    List<String> result = new ArrayList<>();
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(input);
    while (matcher.find()) {
      String match = matcher.group();
      result.add(match);
    }
    return result;
  }

}
