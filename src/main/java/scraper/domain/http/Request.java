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

  private Request(Map<String, String> headers, String url, List<FormBodyPair> formBody) {
    this.headers = headers;
    this.url = url;
    this.formBody = formBody;
  }

  public static Request.Builder builder() {
    return new Request.Builder();
  }

  public static class Builder {
    private Map<String, String> headers = new HashMap<>();
    private String url;
    private List<FormBodyPair> formBodyPairs = new ArrayList<>();

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

    public Request build() {
      return new Request(Collections.unmodifiableMap(headers), url, Collections.unmodifiableList(formBodyPairs));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Request that = (Request) o;
    return Objects.equals(headers, that.headers) && Objects.equals(url, that.url) && Objects.equals(formBody, that.formBody);
  }

  @Override
  public int hashCode() {
    return Objects.hash(headers, url, formBody);
  }

  public static record FormBodyPair(String name, String value) {

  }

}
