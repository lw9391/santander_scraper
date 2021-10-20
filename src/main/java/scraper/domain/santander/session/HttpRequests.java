package scraper.domain.santander.session;

import scraper.domain.http.Request;

public class HttpRequests {

  public final String baseUrl;
  public final String dashboardPath = "/multi";
  public final String logoutPath = "/logout";

  public HttpRequests(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public Request loginPage() {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + "/login")
            .build();
  }

  public Request redirectXml(String path) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "input_nik")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .build();
  }

  public Request nik(String path, String nik) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .addFormBodyPair("nik", nik)
            .addFormBodyPair("dp", "")
            .addFormBodyPair("loginButton", "1")
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "okBtn2")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .build();
  }

  public Request passwordPage(String path) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .build();
  }

  public Request password(String path, String password) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .addFormBodyPair("pinFragment:pin", password)
            .addFormBodyPair("loginButton", "Dalej")
            .build();
  }

  public Request smsCode(String path, String token) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .addFormBodyPair("response", token)
            .addFormBodyPair("loginButton", "Dalej")
            .build();
  }

  public Request productsPage(String path) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + dashboardPath + path)
            .build();
  }

  public Request logout() {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + logoutPath)
            .build();
  }

}