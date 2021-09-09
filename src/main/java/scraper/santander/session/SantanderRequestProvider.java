package scraper.santander.session;

import scraper.connections.RequestDto;

import static scraper.Header.ACCEPT;
import static scraper.Header.ACCEPT_LANGUAGE;
import static scraper.Header.CACHE_CONTROL;
import static scraper.Header.CONNECTION;
import static scraper.Header.CONTENT_TYPE_URLENCODED;
import static scraper.Header.USER_AGENT;
import static scraper.santander.SantanderAccountsScraper.DASHBOARD_PATH;
import static scraper.santander.SantanderAccountsScraper.HOST;
import static scraper.santander.SantanderAccountsScraper.LOGOUT;
import static scraper.santander.SantanderAccountsScraper.PATH;

public class SantanderRequestProvider {
    public static RequestDto GETLoginPage() {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + PATH + "/login")
                .build();
    }

    public static RequestDto GETXmlWithPathForNikPage(String path, String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + PATH + path)
                .setHeader("Referer", referer)
                .setHeader("Wicket-Ajax", "true")
                .setHeader("Wicket-Ajax-BaseURL", referer.substring(referer.indexOf("login?")))
                .setHeader("Wicket-FocusedElementId", "input_nik")
                .setHeader("X-Requested-With", "XMLHttpRequest")
                .build();
    }

    public static RequestDto POSTNik(String path, String nik, String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + PATH + path)
                .addFormBodyPair("nik", nik)
                .addFormBodyPair("dp", "")
                .addFormBodyPair("loginButton", "1")
                .setHeader(CONTENT_TYPE_URLENCODED.name, CONTENT_TYPE_URLENCODED.value)
                .setHeader("Referer", referer)
                .setHeader("Wicket-Ajax", "true")
                .setHeader("Wicket-Ajax-BaseURL", referer.substring(referer.indexOf("login?")))
                .setHeader("Wicket-FocusedElementId", "okBtn2")
                .setHeader("X-Requested-With", "XMLHttpRequest")
                .build();
    }

    public static RequestDto GETPasswordPage(String path, String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + PATH + path)
                .setHeader("Referer", referer)
                .build();
    }

    public static RequestDto GETSendSessionMap(String path, String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + PATH + path)
                .setHeader("Referer", referer)
                .setHeader("Wicket-Ajax", "true")
                .setHeader("Wicket-Ajax-BaseURL", referer.substring(referer.indexOf("crypt.")))
                .setHeader("Wicket-FocusedElementId", "ordinarypin")
                .setHeader("X-Requested-With", "XMLHttpRequest")
                .build();
    }

    public static RequestDto POSTPassword(String path, String password, String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + PATH + path)
                .addFormBodyPair("pinFragment:pin", password)
                .addFormBodyPair("loginButton", "Dalej")
                .setHeader(CONTENT_TYPE_URLENCODED.name, "application/x-www-form-urlencoded")
                .setHeader("Referer", referer)
                .build();
    }

    public static RequestDto POSTToken(String path, String token, String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + PATH + path)
                .addFormBodyPair("response", token)
                .addFormBodyPair("loginButton", "Dalej")
                .setHeader(CONTENT_TYPE_URLENCODED.name, "application/x-www-form-urlencoded")
                .setHeader("Referer", referer)
                .build();
    }

    public static RequestDto GETLogout(String path, String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + DASHBOARD_PATH + path)
                .setHeader("Referer", referer)
                .build();
    }

    public static RequestDto GETProductsPage(String path, String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(HOST + DASHBOARD_PATH + path)
                .setHeader("Referer", referer)
                .build();
    }

    public static RequestDto GETEmergencyLogout(String referer) {
        RequestDto.Builder builder = RequestDto.builder();
        setCommonHeaders(builder);
        return builder.setUrl(LOGOUT)
                .setHeader("Referer", referer)
                .build();
    }

    private static RequestDto.Builder setCommonHeaders(RequestDto.Builder builder) {
        return builder.setHeader(ACCEPT.name, ACCEPT.value)
                .setHeader(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .setHeader(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .setHeader(CONNECTION.name, CONNECTION.value)
                .setHeader(USER_AGENT.name, USER_AGENT.value);
    }

}
