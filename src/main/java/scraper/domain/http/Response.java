package scraper.domain.http;

import java.util.Collections;
import java.util.Map;

public class Response {

  public final Map<String, String> headers;
  public final String body;
  public final String url;
  public final int status;

  private Response(Map<String, String> headers, String body, String url, int status) {
    this.headers = headers;
    this.body = body;
    this.url = url;
    this.status = status;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Map<String, String> headers;
    private String body;
    private String url;
    private int status;

    public Builder setHeaders(Map<String, String> headers) {
      this.headers = Collections.unmodifiableMap(headers);
      return this;
    }

    public Builder setBody(String body) {
      this.body = body;
      return this;
    }

    public Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder setStatus(int status) {
      this.status = status;
      return this;
    }

    public Response build() {
      return new Response(headers, body, url, status);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Status: ").append(status);
    sb.append("Request url: ").append(url);
    sb.append("\n");
    sb.append("Response headers:");
    sb.append("\n");
    headers.forEach((name, value) -> sb.append(name).append(", ").append(value).append("\n"));
    sb.append("Response body:");
    sb.append(body);
    return sb.toString();
  }

}
