package scraper.domain.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Request {

  public final Map<String, String> headers;
  public final String url;
  public final List<FormBodyPair> formBody;
  public final Method method;

  private Request(Map<String, String> headers, String url, List<FormBodyPair> formBody, Method method) {
    this.headers = headers;
    this.url = url;
    this.formBody = formBody;
    this.method = method;
  }

  public static Request.Builder builder() {
    return new Request.Builder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Request request = (Request) o;
    return headers.equals(request.headers) && url.equals(request.url) && formBody.equals(request.formBody) && method == request.method;
  }

  @Override
  public int hashCode() {
    return Objects.hash(headers, url, formBody, method);
  }

  public enum Method {
    GET,
    POST
  }

  public static class Builder {
    private final Map<String, String> headers = new HashMap<>();
    private String url;
    private final List<FormBodyPair> formBodyPairs = new ArrayList<>();
    private Method method;

    public Request.Builder setHeader(String headerName, String headerValue) {
      headers.put(headerName, headerValue);
      return this;
    }

    public Request.Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public Request.Builder addFormBodyPair(String name, String value) {
      formBodyPairs.add(new FormBodyPair(name, value));
      return this;
    }

    public Request.Builder setMethod(Method method) {
      this.method = method;
      return this;
    }

    public Request build() {
      return new Request(Collections.unmodifiableMap(headers), url, Collections.unmodifiableList(formBodyPairs), method);
    }
  }

  public static record FormBodyPair(String name, String value) {

  }

}
