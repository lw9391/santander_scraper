package scraper.domain.connections.okhttp;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import scraper.domain.connections.HttpRequestSender;
import scraper.domain.connections.RequestDto;
import scraper.domain.connections.ResponseDto;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class OkHttpRequestsSender implements HttpRequestSender {

  private final OkHttpClient client;

  public OkHttpRequestsSender() {
    CookieManager cookieManager = new CookieManager();
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);

    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    this.client = builder.cache(null)
            .cookieJar(cookieJar)
            .build();
  }

  @Override
  public ResponseDto sendGET(RequestDto requestDto) {
    Request request = OkHttpMapper.mapDtoToGetRequest(requestDto);

    return sendHttpRequest(request);
  }

  @Override
  public ResponseDto sendPOST(RequestDto requestDto) {
    Request request = OkHttpMapper.mapDtoToPostRequest(requestDto);

    return sendHttpRequest(request);
  }

  private ResponseDto sendHttpRequest(Request request) {
    try {
      Response response = client.newCall(request).execute();
      ResponseDto responseDto = OkHttpMapper.mapToDto(response);
      response.close();
      return responseDto;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
