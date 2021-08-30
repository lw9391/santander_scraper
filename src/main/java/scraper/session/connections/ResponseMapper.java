package scraper.session.connections;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResponseMapper {
    public static ResponseDto mapToDto(Response response) throws IOException {
        ResponseDto.ResponseDtoBuilder builder = ResponseDto.builder();

        Map<String,String> responseHeaders = headersToMap(response.headers());

        String requestUrl = response.request()
                .url()
                .toString();

        String responseBodyString = "";

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            byte[] bytes = response.body().bytes();
            responseBodyString = new String(bytes, "Cp1250");
        }

        return builder.setResponseBody(responseBodyString)
                .setResponseHeaders(responseHeaders)
                .setRequestUrl(requestUrl)
                .setStatus(response.code())
                .build();
    }

    private static Map<String,String> headersToMap(Headers headers) {
        Map<String,String> result = new HashMap<>();
        headers.forEach(header -> result.put(header.component1(), header.component2()));
        return result;
    }
}
