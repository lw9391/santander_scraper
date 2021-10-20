package scraper.domain.santander.session;

import scraper.domain.connections.RequestDto;

public class HttpRequests {

  public final String baseUrl;
  public final String dashboardPath = "/multi";
  public final String logoutPath = "/logout";

  public HttpRequests(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public RequestDto loginPage() {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(baseUrl + "/login")
            .build();
  }

  public RequestDto redirectXml(String path) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(baseUrl + path)
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "input_nik")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .build();
  }

  public RequestDto nik(String path, String nik) {
    RequestDto.Builder builder = RequestDto.builder();
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

  public RequestDto passwordPage(String path) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(baseUrl + path)
            .build();
  }

  public RequestDto password(String path, String password) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(baseUrl + path)
            .addFormBodyPair("pinFragment:pin", password)
            .addFormBodyPair("loginButton", "Dalej")
            .build();
  }

  public RequestDto smsCode(String path, String token) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(baseUrl + path)
            .addFormBodyPair("response", token)
            .addFormBodyPair("loginButton", "Dalej")
            .build();
  }

  public RequestDto productsPage(String path) {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(baseUrl + dashboardPath + path)
            .build();
  }

  public RequestDto logout() {
    RequestDto.Builder builder = RequestDto.builder();
    return builder.setUrl(baseUrl + logoutPath)
            .build();
  }

}