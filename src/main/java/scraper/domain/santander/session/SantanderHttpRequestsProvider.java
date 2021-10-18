package scraper.domain.santander.session;

import scraper.domain.connections.RequestDto;

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
    return builder.setUrl(HOST + PATH + "/login")
            .build();
  }

  public RequestDto GETXmlWithPathForNikPage(String path) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(HOST + PATH + path)
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "input_nik")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .build();
  }

  public RequestDto POSTNik(String path, String nik) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(HOST + PATH + path)
            .addFormBodyPair("nik", nik)
            .addFormBodyPair("dp", "")
            .addFormBodyPair("loginButton", "1")
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "okBtn2")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .build();
  }

  public RequestDto GETPasswordPage(String path) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(HOST + PATH + path)
            .build();
  }

  public RequestDto POSTPassword(String path, String password) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(HOST + PATH + path)
            .addFormBodyPair("pinFragment:pin", password)
            .addFormBodyPair("loginButton", "Dalej")
            .build();
  }

  public RequestDto POSTSmsCode(String path, String token) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(HOST + PATH + path)
            .addFormBodyPair("response", token)
            .addFormBodyPair("loginButton", "Dalej")
            .build();
  }

  public RequestDto GETProductsPage(String path) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(HOST + DASHBOARD_PATH + path)
            .build();
  }

  public RequestDto GETLogout() {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(HOST + PATH + LOGOUT)
            .build();
  }

}