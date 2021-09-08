package scraper.session.connections;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OkHttpRequestsSender implements HttpRequestSender {

    private final OkHttpClient client;

    public OkHttpRequestsSender(OkHttpClient client) {
        this.client = client;
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
