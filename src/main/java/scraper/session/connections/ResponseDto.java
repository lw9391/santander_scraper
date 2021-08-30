package scraper.session.connections;

import java.util.Collections;
import java.util.Map;

public class ResponseDto {
    private final Map<String,String> responseHeaders;
    private final String responseBody;
    private final String requestUrl;
    private final int status;

    private ResponseDto(Map<String, String> responseHeaders, String responseBody, String requestUrl, int status) {
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
        this.requestUrl = requestUrl;
        this.status = status;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public int getStatus() {
        return status;
    }

    public static ResponseDtoBuilder builder() {
        return new ResponseDtoBuilder();
    }

    public static class ResponseDtoBuilder {
        private Map<String, String> responseHeaders;
        private String responseBody;
        private String requestUrl;
        private int status;

        public ResponseDtoBuilder setResponseHeaders(Map<String, String> responseHeaders) {
            this.responseHeaders = Collections.unmodifiableMap(responseHeaders);
            return this;
        }

        public ResponseDtoBuilder setResponseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public ResponseDtoBuilder setRequestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
            return this;
        }

        public ResponseDtoBuilder setStatus(int status) {
            this.status = status;
            return this;
        }

        public ResponseDto build() {
            return new ResponseDto(responseHeaders, responseBody, requestUrl, status);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Status: ").append(status);
        sb.append("Request url: ").append(requestUrl);
        sb.append("\n");
        sb.append("Response headers:");
        sb.append("\n");
        responseHeaders.forEach((name,value) -> sb.append(name).append(", ").append(value).append("\n"));
        sb.append("Response body:");
        sb.append(responseBody);
        return sb.toString();
    }
}
