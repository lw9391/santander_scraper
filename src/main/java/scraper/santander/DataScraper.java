package scraper.santander;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scraper.AccountDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static scraper.santander.PathsNames.PASSWORD;
import static scraper.santander.PathsNames.SESSION_MAP;

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
        return getSubstringBetween(redirectElement,"/","]");
    }

    private static String getXmlElement(String xml, String tagName) {
        Document xmlDoc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());
        return xmlDoc.select(tagName).html();
    }

    public static Map<PathsNames, String> scrapePathsFromPasswordPage(String passwordPageHtml) {
        String pathForSessionMap = scrapePathForSessionMapRequest(passwordPageHtml);

        String attribute = getAttributeFromHtml(passwordPageHtml, "pinForm", "action");
        int startIndex = attribute.indexOf("/crypt.");
        String pathForPasswordRequest = attribute.substring(startIndex);

        return Map.of(SESSION_MAP, pathForSessionMap, PASSWORD, pathForPasswordRequest);
    }

    private static String scrapePathForSessionMapRequest(String html) {
        Document document = Jsoup.parse(html);
        Elements head = document.select("head");
        String script = head.select("script").not("[src]").not("[id]").html();
        return getSubstringBetween(script, "/crypt", "\"");
    }

    private static String getSubstringBetween(String input, String start, String end) {
        int startIndex = input.indexOf(start);
        String part = input.substring(startIndex);
        int endIndex = part.indexOf(end);
        return part.substring(0, endIndex);
    }

    public static String scrapeTokenPathFromPasswordResponse(String passwordResponesPageHtml) {
        String attribute = getAttributeFromHtml(passwordResponesPageHtml, "authenticationForm", "action");
        int startIndex = attribute.indexOf("/crypt.");
        return attribute.substring(startIndex);
    }

    private static String getAttributeFromHtml(String html, String id, String attribute) {
        return Jsoup.parse(html)
                .getElementById(id)
                .attr(attribute);
    }

    public static String scrapeInvalidLoginDiv(String tokenResponseHtml) {
        Element logoutInfo = Jsoup.parse(tokenResponseHtml).getElementById("wylogowanie");
        if (logoutInfo == null) {
            return "";
        }
        return logoutInfo.outerHtml();
    }

    public static Map<PathsNames,String> scrapePathsFromDashboardPage(String dashboardPageHtml) {
        String logoutDiv = getFromHtmlByClass(dashboardPageHtml, "logout");
        String logoutPath = Jsoup.parse(logoutDiv).select("a").attr("href").substring(1);

        String productsLi = getFromHtmlById(dashboardPageHtml, "menu_all_products");
        String productsPath = Jsoup.parse(productsLi).select("a").attr("href").substring(1);

        return Map.of(PathsNames.LOGOUT, logoutPath, PathsNames.PRODUCTS, productsPath);
    }

    private static String getFromHtmlByClass(String html, String clazz) {
        return Jsoup.parse(html)
                .getElementsByClass(clazz)
                .outerHtml();
    }

    private static String getFromHtmlById(String html, String id) {
        return Jsoup.parse(html)
                .getElementById(id)
                .html();
    }

    public static List<AccountDetails> scrapeAccountsInformationFromProductsPage(String productsPageHtml) {
        List<AccountDetails> accountDetails = new ArrayList<>();
        List<AccountDetails> personalAccounts = getInformationAbout(productsPageHtml, "avistaAccountsBoxContent");
        accountDetails.addAll(personalAccounts);
        List<AccountDetails> deposits = getInformationAbout(productsPageHtml, "deposits");
        accountDetails.addAll(deposits);
        List<AccountDetails> savings = getInformationAbout(productsPageHtml, "savings");
        accountDetails.addAll(savings);
        return accountDetails;
    }

    private static List<AccountDetails> getInformationAbout(String html, String categoryId) {
        List<AccountDetails> personalAccounts = new ArrayList<>();
        Element table = Jsoup.parse(html)
                .getElementById(categoryId)
                .selectFirst("table");
        if (table == null) {
            return personalAccounts;
        }
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
