package scraper.santander.session;

import scraper.AccountDetails;
import scraper.InvalidCredentialsException;
import scraper.connections.ResponseDto;
import scraper.santander.DataScraper;
import scraper.santander.PathsNames;
import scraper.santander.SantanderAccountsScraper;
import scraper.util.DataBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequestHandler {
    private final SantanderConnectionHandler connectionHandler;
    private final DataBuilder dataBuilder;
    private final DataScraper dataScraper;

    private SantanderSession session;

    public RequestHandler(SantanderConnectionHandler connectionHandler, SantanderSession session) {
        this.connectionHandler = connectionHandler;
        this.session = session;
        this.dataBuilder = new DataBuilder();
        this.dataScraper = new DataScraper();
    }

    public RequestHandler(SantanderConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.dataBuilder = new DataBuilder();
        this.dataScraper = new DataScraper();
    }

    public String sendLoginPageRequest() {
        ResponseDto response = connectionHandler.GETLoginPage();

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during getting login page.");
        }

        String scrapedPath =  dataScraper.scrapeXmlPathFromLoginPage(response.getResponseBody());
        session.updateReferer(response.getRequestUrl());
        return scrapedPath;
    }

    public String sendRedirectXmlRequest(String queryParam) {
        long timestamp = new Date().getTime();
        String queryForXml = queryParam + "&_=" + timestamp;
        ResponseDto response = connectionHandler.GETXmlWithPathForNikPage(queryForXml, session.getCurrentReferer());

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during getting redirect xml.");
        }

        return dataScraper.scrapeNikPagePathFromRedirectXml(response.getResponseBody());
    }

    public String sendNikRequest(String queryParam, String nik) {
        ResponseDto response = connectionHandler.POSTNik(queryParam, nik, session.getCurrentReferer());

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending nik.");
        }

        return dataScraper.scrapePasswordPagePathFromNikResponse(response.getResponseBody());
    }

    public Map<PathsNames,String> sendPasswordPageRequest(String path) {
        ResponseDto response = connectionHandler.GETPasswordPage(path, session.getCurrentReferer());

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during getting password page");
        }

        Map<PathsNames, String> paths = dataScraper.scrapePathsFromPasswordPage(response.getResponseBody());
        session.updateReferer(response.getRequestUrl());
        return paths;
    }

    public void sendSessionMapRequest(String path) {
        String mapSettings =
                "true%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Ctrue%2Cfalse%2C1300%2C1.5%2C1300%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Ctrue%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Cfalse%2Ctrue%2Cfalse%2Cfalse";
        long timestamp = new Date().getTime();
        String queryParams = dataBuilder.buildQueryParams("sessionMap", mapSettings, "_", String.valueOf(timestamp));
        ResponseDto response = connectionHandler.GETSendSessionMap(path + queryParams, session.getCurrentReferer());

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending session map.");
        }
    }

    public String sendPasswordRequest(String path, String password) {
        ResponseDto response = connectionHandler.POSTPassword(path, password, session.getCurrentReferer());

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending password");
        }
        String tokenPath = dataScraper.scrapeTokenPathFromPasswordResponse(response.getResponseBody());

        session.updateReferer(response.getRequestUrl());
        return tokenPath;
    }

    public Map<PathsNames,String> sendTokenRequest(String tokenConfirmationPath, String token) {
        ResponseDto response = connectionHandler.POSTToken(tokenConfirmationPath, token, session.getCurrentReferer());

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending token");
        }
        if (!validateLoginCorrectness(response)) {
            throw new InvalidCredentialsException("Login failed, provided incorrect password or token.");
        }
        Map<PathsNames,String> paths = dataScraper.scrapePathsFromDashboardPage(response.getResponseBody());

        session.updateReferer(response.getRequestUrl());
        return paths;
    }

    private boolean validateLoginCorrectness(ResponseDto tokenPOSTResponse) {
        String logoutDiv = dataScraper.scrapeInvalidLoginDiv(tokenPOSTResponse.getResponseBody());
        return logoutDiv.isEmpty();
    }

    public List<AccountDetails> scrapeAccountsInformation(String path) {
        ResponseDto response = connectionHandler.GETProductsPage(path, session.getCurrentReferer());

        if (!(response.getStatus() == 200)) {
            connectionHandler.GETEmergencyLogout(session.getCurrentReferer());
            throw new RuntimeException("Status code error during getting product page.");
        }

        return dataScraper.scrapeAccountsInformationFromProductsPage(response.getResponseBody());
    }

    public void sendLogoutRequest(String query) {
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