package scraper.santander.actions;

import scraper.santander.http.Fetcher;
import scraper.santander.http.Request;
import scraper.santander.http.Response;

import static scraper.santander.http.Request.Method.GET;
import static scraper.santander.http.Request.Method.POST;

public class SantanderHttpApi {

  private final String baseUrl;
  private final Fetcher fetcher;

  public SantanderHttpApi(String baseUrl, Fetcher fetcher) {
    this.baseUrl = baseUrl;
    this.fetcher = fetcher;
  }

  Response loginPage() {
    Request.Builder builder = Request.builder();
    Request request = builder.setUrl(baseUrl + "/login")
            .setMethod(GET)
            .build();
    return fetcher.send(request);
  }

  Response redirectXml(String path) {
    Request.Builder builder = Request.builder();
    Request request = builder.setUrl(baseUrl + path)
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "input_nik")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .setMethod(GET)
            .build();
    return fetcher.send(request);
  }

  Response nik(String path, String nik) {
    Request.Builder builder = Request.builder();
    Request request = builder.setUrl(baseUrl + path)
            .addFormBodyPair("nik", nik)
            .setHeader("Wicket-Ajax", "true")
            .setHeader("Wicket-Ajax-BaseURL", ".")
            .setHeader("Wicket-FocusedElementId", "okBtn2")
            .setHeader("X-Requested-With", "XMLHttpRequest")
            .setMethod(POST)
            .build();
    return fetcher.send(request);
  }

  Response passwordPage(String path) {
    Request.Builder builder = Request.builder();
    Request request = builder.setUrl(baseUrl + path)
            .setMethod(GET)
            .build();
    return fetcher.send(request);
  }

  Response password(String path, String password) {
    Request.Builder builder = Request.builder();
    Request request = builder.setUrl(baseUrl + path)
            .addFormBodyPair("pinFragment:pin", password)
            .setMethod(POST)
            .build();
    return fetcher.send(request);
  }

  Response smsCode(String path, String token) {
    Request.Builder builder = Request.builder();
    Request request = builder.setUrl(baseUrl + path)
            .addFormBodyPair("response", token)
            .setMethod(POST)
            .build();
    return fetcher.send(request);
  }

  Response productsPage(String path) {
    Request.Builder builder = Request.builder();
    Request request = builder.setUrl(baseUrl + "/multi" + path)
            .setMethod(GET)
            .build();
    return fetcher.send(request);
  }

  Response logout() {
    Request.Builder builder = Request.builder();
    Request request = builder.setUrl(baseUrl + "/logout")
            .setMethod(GET)
            .build();
    return fetcher.send(request);
  }

}