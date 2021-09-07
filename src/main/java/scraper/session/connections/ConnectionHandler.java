package scraper.session.connections;

import java.io.IOException;

public interface ConnectionHandler {
    ResponseDto GETLoginPage();
    ResponseDto GETXmlWithPathForNikPage(String path, String referer);
    ResponseDto POSTNik(String path, String nik, String referer);
    ResponseDto GETPasswordPage(String path, String referer);
    ResponseDto GETSendSessionMap(String path, String referer);
    ResponseDto POSTPassword(String path, String password, String referer);
    ResponseDto POSTToken(String path, String token, String referer);
    ResponseDto GETLogout(String path, String referer);
    ResponseDto GETProductsPage(String path, String referer);
    ResponseDto GETEmergencyLogout(String referer);
}
