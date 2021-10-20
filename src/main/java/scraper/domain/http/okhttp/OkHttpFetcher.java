package scraper.domain.http.okhttp;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import scraper.domain.http.HttpFetcher;
import scraper.domain.http.Request;
import scraper.domain.http.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class OkHttpFetcher implements HttpFetcher {

  private final OkHttpClient client;

  public OkHttpFetcher() {
    CookieManager cookieManager = new CookieManager();
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);

    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    this.client = builder.cache(null)
            .cookieJar(cookieJar)
            .build();
  }

  @Override
  public Response send(Request request) {
    okhttp3.Request okRequest = OkHttpMapper.mapRequest(request);

    return sendHttpRequest(okRequest);
  }

  private Response sendHttpRequest(okhttp3.Request okRequest) {
    try {
      okhttp3.Response okResponse = client.newCall(okRequest).execute();
      Response response = OkHttpMapper.mapResponse(okResponse);
      okResponse.close();
      return response;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
