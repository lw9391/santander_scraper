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

public class OkHttpMapper {
  public static Response mapToDto(okhttp3.Response response) throws IOException {
    Response.ResponseDtoBuilder builder = Response.builder();

    Map<String, String> responseHeaders = headersToMap(response.headers());

    String requestUrl = response.request()
            .url()
            .toString();

    String responseBodyString = extractResponseBody(response);

    return builder.setResponseBody(responseBodyString)
            .setResponseHeaders(responseHeaders)
            .setRequestUrl(requestUrl)
            .setStatus(response.code())
            .build();
  }

  private static Map<String, String> headersToMap(Headers headers) {
    Map<String, String> result = new HashMap<>();
    headers.forEach(header -> result.put(header.component1(), header.component2()));
    return result;
  }

  private static String extractResponseBody(okhttp3.Response response) throws IOException {
    String responseBodyString = "";

    ResponseBody responseBody = response.body();
    if (responseBody != null) {
      byte[] bytes = response.body().bytes();
      responseBodyString = new String(bytes, StandardCharsets.UTF_8);
    }
    return responseBodyString;
  }

  public static okhttp3.Request mapDtoToGetRequest(Request request) {
    if (!request.formBody.isEmpty()) {
      throw new UnsupportedOperationException("GET with request body is not allowed.");
    }
    Headers headers = Headers.of(request.headers);
    String url = request.url;

    return new okhttp3.Request.Builder()
            .url(url)
            .headers(headers)
            .get()
            .build();
  }

  public static okhttp3.Request mapDtoToPostRequest(Request request) {
    Headers headers = Headers.of(request.headers);
    RequestBody body = buildFormBody(request.formBody);
    String url = request.url;

    return new okhttp3.Request.Builder()
            .url(url)
            .headers(headers)
            .post(body)
            .build();
  }

  private static RequestBody buildFormBody(List<Request.FormBodyPair> formBodyPairs) {
    if (formBodyPairs.isEmpty()) {
      return RequestBody.create(new byte[]{0});
    }
    FormBody.Builder formBuilder = new FormBody.Builder();
    formBodyPairs.forEach(formBodyPair -> formBuilder.add(formBodyPair.name(), formBodyPair.value()));
    return formBuilder.build();
  }
}