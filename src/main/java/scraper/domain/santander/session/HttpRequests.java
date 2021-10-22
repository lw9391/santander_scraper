package scraper.domain.santander.session;

import scraper.domain.http.Request;

import static scraper.domain.http.Request.Method.*;

public class HttpRequests {

  private final String baseUrl;

  public HttpRequests(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  Request loginPage() {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + "/login")
            .setMethod(GET)
            .build();
  }

  Request redirectXml(String path) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "input_nik")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .setMethod(GET)
            .build();
  }

  Request nik(String path, String nik) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .addFormBodyPair("nik", nik)
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "okBtn2")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .setMethod(POST)
            .build();
  }

  Request passwordPage(String path) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .setMethod(GET)
            .build();
  }

  Request password(String path, String password) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .addFormBodyPair("pinFragment:pin", password)
            .setMethod(POST)
            .build();
  }

  Request smsCode(String path, String token) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + path)
            .addFormBodyPair("response", token)
            .setMethod(POST)
            .build();
  }

  Request productsPage(String path) {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + "/multi" + path)
            .setMethod(GET)
            .build();
  }

  Request logout() {
    Request.Builder builder = Request.builder();
    return builder.setUrl(baseUrl + "/logout")
            .setMethod(GET)
            .build();
  }

}