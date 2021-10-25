package scraper.santander.http.okhttp;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import scraper.santander.http.Request;
import scraper.santander.http.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OkHttpMapper {

  static Response mapResponse(okhttp3.Response okResponse) throws IOException {
    Map<String, String> headers = headersToMap(okResponse.headers());
    String requestUrl = okResponse.request()
            .url()
            .toString();
    String body = extractResponseBody(okResponse);
    return Response.builder().setBody(body)
            .setHeaders(headers)
            .setUrl(requestUrl)
            .setStatus(okResponse.code())
            .build();
  }

  private static Map<String, String> headersToMap(Headers headers) {
    Map<String, String> result = new HashMap<>();
    headers.forEach(header -> result.put(header.getFirst(), header.getSecond()));
    return result;
  }

  private static String extractResponseBody(okhttp3.Response response) throws IOException {
    ResponseBody body = response.body();
    if (body != null)
      return new String(body.bytes(), StandardCharsets.UTF_8);
    return "";
  }

  static okhttp3.Request mapRequest(Request request) {
    return new okhttp3.Request.Builder()
            .method(request.method.name(), buildBody(request.formBody))
            .url(request.url)
            .headers(Headers.of(request.headers))
            .build();
  }

  private static RequestBody buildBody(List<Request.FormBodyPair> formBodyPairs) {
    if (formBodyPairs.isEmpty())
      return RequestBody.create(new byte[]{0});
    FormBody.Builder formBuilder = new FormBody.Builder();
    formBodyPairs.forEach(formBodyPair -> formBuilder.add(formBodyPair.name(), formBodyPair.value()));
    return formBuilder.build();
  }

}