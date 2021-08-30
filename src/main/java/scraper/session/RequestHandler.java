package scraper.session;

import scraper.AccountDetails;
import scraper.SantanderAccountsScraper;
import scraper.session.connections.ConnectionHandler;
import scraper.session.connections.ResponseDto;
import scraper.util.DataBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequestHandler {
    private final ConnectionHandler connectionHandler;
    private final DataBuilder dataBuilder;
    private final DataScraper dataScraper;

    private SantanderSession session;

    public RequestHandler(ConnectionHandler connectionHandler, SantanderSession session) {
        this.connectionHandler = connectionHandler;
        this.session = session;
        this.dataBuilder = new DataBuilder();
        this.dataScraper = new DataScraper();
    }

    public RequestHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.dataBuilder = new DataBuilder();
        this.dataScraper = new DataScraper();
    }

    public String sendLoginPageRequest() throws IOException {
        ResponseDto response = connectionHandler.GETLoginPage();
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during getting login page.");
        }

        String scrapedPath =  dataScraper.scrapXmlPathFromLoginPage(response.getResponseBody());
        session.updateReferer(response.getRequestUrl());
        return scrapedPath;
    }

    public String sendRedirectXmlRequest(String queryParam) throws IOException {
        long timestamp = new Date().getTime();
        String queryForXml = queryParam + "&_=" + timestamp;
        ResponseDto response = connectionHandler.GETXmlWithPathForNikPage(queryForXml, session.getCurrentReferer());
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during getting redirect xml.");
        }

        return dataScraper.scrapNikPagePathFromRedirectXml(response.getResponseBody());
    }

    public String sendNikRequest(String queryParam, String nik) throws IOException {
        ResponseDto response = connectionHandler.POSTNik(queryParam, nik, session.getCurrentReferer());
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending nik.");
        }

        return dataScraper.scrapPasswordPagePathFromNikResponse(response.getResponseBody());
    }

    public Map<PathsNames,String> sendPasswordPageRequest(String path) throws IOException {
        ResponseDto response = connectionHandler.GETPasswordPage(path, session.getCurrentReferer());
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during getting password page");
        }

        Map<PathsNames, String> paths = dataScraper.scrapPathsFromPasswordPage(response.getResponseBody());
        session.updateReferer(response.getRequestUrl());
        return paths;
    }

    public void sendSessionMapRequest(String path) throws IOException {
        String mapSettings =
                "true%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Ctrue%2Cfalse%2C1300%2C1.5%2C1300%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Ctrue%2Cfalse%2Cfalse";
        long timestamp = new Date().getTime();
        String queryParams = dataBuilder.buildQueryParams("sessionMap", mapSettings, "_", String.valueOf(timestamp));
        ResponseDto response = connectionHandler.GETSendSessionMap(path + queryParams, session.getCurrentReferer());
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending session map.");
        }
    }

    public String sendPasswordRequest(String path, String password) throws IOException {
        ResponseDto response = connectionHandler.POSTPassword(path, password, session.getCurrentReferer());
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending password");
        }
        String tokenPath = dataScraper.scrapTokenPathFromPasswordResponse(response.getResponseBody());

        session.updateReferer(response.getRequestUrl());
        return tokenPath;
    }

    public Map<PathsNames,String> sendTokenRequest(String tokenConfirmationPath, String token) throws IOException {
        ResponseDto response = connectionHandler.POSTToken(tokenConfirmationPath, token, session.getCurrentReferer());
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending token");
        }
        Map<PathsNames,String> paths = dataScraper.scrapPathsFromDashboardPage(response.getResponseBody());

        session.updateReferer(response.getRequestUrl());
        return paths;
    }

    public List<AccountDetails> scrapAccountsInformation(String path) throws IOException {
        ResponseDto response = connectionHandler.GETProductsPage(path, session.getCurrentReferer());
        if (!(response.getStatus() == 200)) {
            connectionHandler.GETEmergencyLogout(session.getCurrentReferer());
            throw new RuntimeException("Status code error during getting product page.");
        }

        return dataScraper.scrapAccountsInformationFromProductsPage(response.getResponseBody());
    }

    public void sendLogoutRequest(String query) throws IOException {
        ResponseDto response = connectionHandler.GETLogout(query, session.getCurrentReferer());
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during logout");
        }

        if (!response.getRequestUrl().equals(SantanderAccountsScraper.LOGOUT)) {
            connectionHandler.GETEmergencyLogout(session.getCurrentReferer());
        }
    }

    public void setSession(SantanderSession session) {
        this.session = session;
    }
}