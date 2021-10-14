package scraper.santander.session;

import scraper.connections.RequestDto;

import static scraper.Header.ACCEPT;
import static scraper.Header.ACCEPT_LANGUAGE;
import static scraper.Header.USER_AGENT;

public class SantanderHttpRequestsProvider {
  public final String HOST;
  public final String PATH = "/centrum24-web";
  public final String DASHBOARD_PATH = "/centrum24-web/multi";
  public final String LOGOUT = "/logout";

  public SantanderHttpRequestsProvider(String HOST) {
    this.HOST = HOST;
  }

  public RequestDto GETLoginPage() {
    RequestDto.Builder builder = RequestDto.builder();
    setCommonHeaders(builder);
    return builder.setUrl(HOST + PATH + "/login")
            .build();
  }

  public RequestDto GETXmlWithPathForNikPage(String path, String referer) {
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

  public RequestDto POSTNik(String path, String nik, String referer) {
    RequestDto.Builder builder = RequestDto.builder();
    setCommonHeaders(builder);
    return builder.setUrl(HOST + PATH + path)
            .addFormBodyPair("nik", nik)
            .addFormBodyPair("dp", "")
            .addFormBodyPair("loginButton", "1")
            .setHeader("Referer", referer)
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", referer.substring(referer.indexOf("login?")))
            .setHeader("Wicket-FocusedElementId", "okBtn2")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .build();
  }

  public RequestDto GETPasswordPage(String path, String referer) {
    RequestDto.Builder builder = RequestDto.builder();
    setCommonHeaders(builder);
    return builder.setUrl(HOST + PATH + path)
            .setHeader("Referer", referer)
            .build();
  }

  public RequestDto GETSendSessionMap(String path, String referer) {
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

  public RequestDto POSTPassword(String path, String password, String referer) {
    RequestDto.Builder builder = RequestDto.builder();
    setCommonHeaders(builder);
    return builder.setUrl(HOST + PATH + path)
            .addFormBodyPair("pinFragment:pin", password)
            .addFormBodyPair("loginButton", "Dalej")
            .setHeader("Referer", referer)
            .build();
  }

  public RequestDto POSTSmsCode(String path, String token, String referer) {
    RequestDto.Builder builder = RequestDto.builder();
    setCommonHeaders(builder);
    return builder.setUrl(HOST + PATH + path)
            .addFormBodyPair("response", token)
            .addFormBodyPair("loginButton", "Dalej")
            .setHeader("Referer", referer)
            .build();
  }

  public RequestDto GETLogout(String path, String referer) {
    RequestDto.Builder builder = RequestDto.builder();
    setCommonHeaders(builder);
    return builder.setUrl(HOST + DASHBOARD_PATH + path)
            .setHeader("Referer", referer)
            .build();
  }

  public RequestDto GETProductsPage(String path, String referer) {
    RequestDto.Builder builder = RequestDto.builder();
    setCommonHeaders(builder);
    return builder.setUrl(HOST + DASHBOARD_PATH + path)
            .setHeader("Referer", referer)
            .build();
  }

  public RequestDto GETEmergencyLogout(String referer) {
    RequestDto.Builder builder = RequestDto.builder();
    setCommonHeaders(builder);
    return builder.setUrl(HOST + PATH + LOGOUT)
            .setHeader("Referer", referer)
            .build();
  }

  private static RequestDto.Builder setCommonHeaders(RequestDto.Builder builder) {
    return builder.setHeader(ACCEPT.name, ACCEPT.value)
            .setHeader(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
            .setHeader(USER_AGENT.name, USER_AGENT.value);
  }
}