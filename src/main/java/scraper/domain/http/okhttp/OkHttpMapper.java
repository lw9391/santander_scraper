package scraper.domain.http.okhttp;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import scraper.domain.http.Request;
import scraper.domain.http.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static scraper.domain.http.Request.Method.*;

public class OkHttpMapper {

  public static Response mapResponse(okhttp3.Response okResponse) throws IOException {
    Response.Builder builder = Response.builder();

    Map<String, String> headers = headersToMap(okResponse.headers());

    String requestUrl = okResponse.request()
            .url()
            .toString();

    String body = extractResponseBody(okResponse);

    return builder.setBody(body)
            .setHeaders(headers)
            .setUrl(requestUrl)
            .setStatus(okResponse.code())
            .build();
  }

  private static Map<String, String> headersToMap(Headers headers) {
    Map<String, String> result = new HashMap<>();
    headers.forEach(header -> result.put(header.component1(), header.component2()));
    return result;
  }

  private static String extractResponseBody(okhttp3.Response response) throws IOException {
    String responseBodyString = "";

    ResponseBody body = response.body();
    if (body != null) {
      byte[] bytes = response.body().bytes();
      responseBodyString = new String(bytes, StandardCharsets.UTF_8);
    }
    return responseBodyString;
  }

  public static okhttp3.Request mapRequest(Request request) {
    Headers headers = Headers.of(request.headers);
    String url = request.url;
    okhttp3.Request.Builder builder;

    if (request.method == GET)
      builder = initBuilderWithGet(request);
    else
      builder = initBuilderWithPost(request);

    return builder.url(url)
            .headers(headers)
            .build();
  }

  private static okhttp3.Request.Builder initBuilderWithGet(Request request) {
    if (!request.formBody.isEmpty())
      throw new UnsupportedOperationException("GET with request body is not allowed.");

    return new okhttp3.Request.Builder().get();
  }

  private static okhttp3.Request.Builder initBuilderWithPost(Request request) {
    return new okhttp3.Request.Builder()
            .post(buildFormBody(request.formBody));
  }

  private static RequestBody buildFormBody(List<Request.FormBodyPair> formBodyPairs) {
    if (formBodyPairs.isEmpty())
      return RequestBody.create(new byte[]{0});

    FormBody.Builder formBuilder = new FormBody.Builder();
    formBodyPairs.forEach(formBodyPair -> formBuilder.add(formBodyPair.name(), formBodyPair.value()));
    return formBuilder.build();
  }

}