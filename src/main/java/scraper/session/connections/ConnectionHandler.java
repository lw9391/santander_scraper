package scraper.session.connections;

import java.io.IOException;

public interface ConnectionHandler {
    ResponseDto GETLoginPage() throws IOException;
    ResponseDto GETXmlWithPathForNikPage(String path, String referer) throws IOException;
    ResponseDto POSTNik(String path, String nik, String referer) throws IOException;
    ResponseDto GETPasswordPage(String path, String referer) throws IOException;
    ResponseDto GETSendSessionMap(String path, String referer) throws IOException;
    ResponseDto POSTPassword(String path, String password, String referer) throws IOException;
    ResponseDto POSTToken(String path, String token, String referer) throws IOException;
    ResponseDto GETLogout(String path, String referer) throws IOException;
    ResponseDto GETProductsPage(String path, String referer) throws IOException;
    ResponseDto GETEmergencyLogout(String referer) throws IOException;
}
