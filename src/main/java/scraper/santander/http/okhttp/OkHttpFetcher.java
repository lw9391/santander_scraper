package scraper.santander.http.okhttp;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import scraper.santander.http.Fetcher;
import scraper.santander.http.Request;
import scraper.santander.http.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class OkHttpFetcher implements Fetcher {

  private final OkHttpClient client;

  public OkHttpFetcher() {
    var cookieManager = new CookieManager();
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    var cookieJar = new JavaNetCookieJar(cookieManager);
    client = new OkHttpClient.Builder().cache(null)
            .cookieJar(cookieJar)
            .build();
  }

  @Override
  public Response send(Request request) {
    okhttp3.Request okRequest = OkHttpMapper.mapRequest(request);
    Response response = sendHttpRequest(okRequest);
    if (response.status != 200)
      throw new RuntimeException(String.format("Error response: %s", response));
    return response;
  }

  private Response sendHttpRequest(okhttp3.Request okRequest) {
    try {
      okhttp3.Response okResponse = client.newCall(okRequest).execute();
      Response response = OkHttpMapper.mapResponse(okResponse);
      okResponse.close();
      return response;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
