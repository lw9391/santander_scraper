package scraper.santander.actions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import scraper.santander.http.Fetcher;
import scraper.santander.http.Request;

import static scraper.santander.http.Request.Method.GET;
import static scraper.santander.http.Request.Method.POST;

public record SantanderHttpApi(String baseUrl, Fetcher fetcher) {

  Document loginPage() {
    Request request = Request.builder()
            .setUrl(baseUrl + "/login")
            .setMethod(GET)
            .build();
    return fetchDocument(request);
  }

  Document redirectXml(String path) {
    Request request = new Request.Builder()
            .setUrl(baseUrl + path)
            .putHeader("Wicket-Ajax", "true")
            .putHeader("Wicket-Ajax-BaseURL", ".")
            .putHeader("Wicket-FocusedElementId", "input_nik")
            .putHeader("X-Requested-With", "XMLHttpRequest")
            .setMethod(GET)
            .build();
    return fetchDocument(request);
  }

  Document nik(String path, String nik) {
    Request request = Request.builder()
            .setUrl(baseUrl + path)
            .addFormBodyPair("nik", nik)
            .putHeader("Wicket-Ajax", "true")
            .putHeader("Wicket-Ajax-BaseURL", ".")
            .putHeader("Wicket-FocusedElementId", "okBtn2")
            .putHeader("X-Requested-With", "XMLHttpRequest")
            .setMethod(POST)
            .build();
    return fetchDocument(request);
  }

  Document passwordPage(String path) {
    Request request = Request.builder()
            .setUrl(baseUrl + path)
            .setMethod(GET)
            .build();
    return fetchDocument(request);
  }

  Document password(String path, String password) {
    Request request = Request.builder()
            .setUrl(baseUrl + path)
            .addFormBodyPair("pinFragment:pin", password)
            .setMethod(POST)
            .build();
    return fetchDocument(request);
  }

  Document smsCode(String path, String token) {
    Request request = Request.builder()
            .setUrl(baseUrl + path)
            .addFormBodyPair("response", token)
            .setMethod(POST)
            .build();
    return fetchDocument(request);
  }

  Document productsPage(String path) {
    Request request = Request.builder()
            .setUrl(baseUrl + "/multi" + path)
            .setMethod(GET)
            .build();
    return fetchDocument(request);
  }

  Document logout() {
    Request request = Request.builder()
            .setUrl(baseUrl + "/logout")
            .setMethod(GET)
            .build();
    return fetchDocument(request);
  }

  private Document fetchDocument(Request request) {
    return Jsoup.parse(fetcher.send(request).body);
  }

}