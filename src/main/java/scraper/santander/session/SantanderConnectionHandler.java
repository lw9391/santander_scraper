package scraper.santander.session;

import scraper.connections.HttpRequestSender;
import scraper.connections.RequestDto;
import scraper.connections.ResponseDto;

public class SantanderConnectionHandler {
    private final HttpRequestSender requestSender;

    public SantanderConnectionHandler(HttpRequestSender requestSender) {
        this.requestSender = requestSender;
    }

    public ResponseDto GETLoginPage() {
        RequestDto requestDto = SantanderRequestProvider.GETLoginPage();
        return requestSender.sendGET(requestDto);
    }

    public ResponseDto GETXmlWithPathForNikPage(String path, String referer) {
        RequestDto requestDto = SantanderRequestProvider.GETXmlWithPathForNikPage(path, referer);
        return requestSender.sendGET(requestDto);
    }

    public ResponseDto POSTNik(String path, String nik, String referer) {
        RequestDto requestDto = SantanderRequestProvider.POSTNik(path, nik, referer);
        return requestSender.sendPOST(requestDto);
    }

    public ResponseDto GETPasswordPage(String path, String referer) {
        RequestDto requestDto = SantanderRequestProvider.GETPasswordPage(path, referer);
        return requestSender.sendGET(requestDto);
    }

    public ResponseDto GETSendSessionMap(String path, String referer) {
        RequestDto requestDto = SantanderRequestProvider.GETSendSessionMap(path, referer);
        return requestSender.sendGET(requestDto);
    }

    public ResponseDto POSTPassword(String path, String password, String referer) {
        RequestDto requestDto = SantanderRequestProvider.POSTPassword(path, password, referer);
        return requestSender.sendPOST(requestDto);
    }

    public ResponseDto POSTToken(String path, String token, String referer) {
        RequestDto requestDto = SantanderRequestProvider.POSTToken(path, token, referer);
        return requestSender.sendPOST(requestDto);
    }

    public ResponseDto GETLogout(String path, String referer) {
        RequestDto requestDto = SantanderRequestProvider.GETLogout(path, referer);
        return requestSender.sendGET(requestDto);
    }

    public ResponseDto GETProductsPage(String path, String referer) {
        RequestDto requestDto = SantanderRequestProvider.GETProductsPage(path, referer);
        return requestSender.sendGET(requestDto);
    }

    public ResponseDto GETEmergencyLogout(String referer) {
        RequestDto requestDto = SantanderRequestProvider.GETEmergencyLogout(referer);
        return requestSender.sendGET(requestDto);
    }
}