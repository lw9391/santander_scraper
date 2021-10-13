package scraper.connections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestDto {
  public final Map<String, String> headers;
  public final String url;
  public final List<FormBodyPair> formBody;

  private RequestDto(Map<String, String> headers, String url, List<FormBodyPair> formBody) {
    this.headers = headers;
    this.url = url;
    this.formBody = formBody;
  }

  public static RequestDto.Builder builder() {
    return new RequestDto.Builder();
  }

  public static class Builder {
    private Map<String, String> headers = new HashMap<>();
    private String url;
    private List<FormBodyPair> formBodyPairs = new ArrayList<>();

    public RequestDto.Builder setHeader(String headerName, String headerValue) {
      headers.put(headerName, headerValue);
      return this;
    }

    public RequestDto.Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public RequestDto.Builder addFormBodyPair(String name, String value) {
      formBodyPairs.add(new FormBodyPair(name, value));
      return this;
    }

    public RequestDto build() {
      return new RequestDto(Collections.unmodifiableMap(headers), url, Collections.unmodifiableList(formBodyPairs));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RequestDto that = (RequestDto) o;
    return Objects.equals(headers, that.headers) && Objects.equals(url, that.url) && Objects.equals(formBody, that.formBody);
  }

  @Override
  public int hashCode() {
    return Objects.hash(headers, url, formBody);
  }

  public static class FormBodyPair {
    public final String name;
    public final String value;

    public FormBodyPair(String name, String value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      FormBodyPair that = (FormBodyPair) o;
      return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, value);
    }
  }
}
