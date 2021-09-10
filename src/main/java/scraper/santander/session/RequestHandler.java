package scraper.santander.session;

import scraper.AccountDetails;
import scraper.InvalidCredentialsException;
import scraper.connections.HttpRequestSender;
import scraper.connections.RequestDto;
import scraper.connections.ResponseDto;
import scraper.santander.DataScraper;
import scraper.santander.PathsNames;
import scraper.santander.SantanderAccountsScraper;
import scraper.util.DataBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequestHandler {
    private final HttpRequestSender sender;
    private final SantanderRequestProvider provider;
    private final DataBuilder dataBuilder;
    private final DataScraper dataScraper;

    private SantanderSession session;

    public RequestHandler(HttpRequestSender sender, SantanderRequestProvider provider, SantanderSession session) {
        this.session = session;
        this.dataBuilder = new DataBuilder();
        this.dataScraper = new DataScraper();
        this.sender = sender;
        this.provider = provider;
    }

    public RequestHandler(HttpRequestSender sender, SantanderRequestProvider provider) {
        this.dataBuilder = new DataBuilder();
        this.dataScraper = new DataScraper();
        this.sender = sender;
        this.provider = provider;
    }

    public String sendLoginPageRequest() {
        RequestDto requestDto = provider.GETLoginPage();
        ResponseDto response = sender.sendGET(requestDto);

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

        RequestDto requestDto = provider.GETXmlWithPathForNikPage(queryForXml, session.getCurrentReferer());
        ResponseDto response = sender.sendGET(requestDto);

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during getting redirect xml.");
        }

        return dataScraper.scrapeNikPagePathFromRedirectXml(response.getResponseBody());
    }

    public String sendNikRequest(String queryParam, String nik) {
        RequestDto requestDto = provider.POSTNik(queryParam, nik, session.getCurrentReferer());
        ResponseDto response = sender.sendPOST(requestDto);

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending nik.");
        }

        return dataScraper.scrapePasswordPagePathFromNikResponse(response.getResponseBody());
    }

    public Map<PathsNames,String> sendPasswordPageRequest(String path) {
        RequestDto requestDto = provider.GETPasswordPage(path, session.getCurrentReferer());
        ResponseDto response = sender.sendGET(requestDto);

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

        RequestDto requestDto = provider.GETSendSessionMap(path + queryParams, session.getCurrentReferer());
        ResponseDto response = sender.sendGET(requestDto);

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending session map.");
        }
    }

    public String sendPasswordRequest(String path, String password) {
        RequestDto requestDto = provider.POSTPassword(path, password, session.getCurrentReferer());
        ResponseDto response = sender.sendPOST(requestDto);

        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during sending password");
        }
        String tokenPath = dataScraper.scrapeTokenPathFromPasswordResponse(response.getResponseBody());

        session.updateReferer(response.getRequestUrl());
        return tokenPath;
    }

    public Map<PathsNames,String> sendTokenRequest(String tokenConfirmationPath, String token) {
        RequestDto requestDto = provider.POSTToken(tokenConfirmationPath, token, session.getCurrentReferer());
        ResponseDto response = sender.sendPOST(requestDto);

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
        RequestDto requestDto = provider.GETProductsPage(path, session.getCurrentReferer());
        ResponseDto response = sender.sendGET(requestDto);

        if (!(response.getStatus() == 200)) {
            RequestDto logout = provider.GETEmergencyLogout(session.getCurrentReferer());
            sender.sendGET(logout);
            throw new RuntimeException("Status code error during getting product page.");
        }

        return dataScraper.scrapeAccountsInformationFromProductsPage(response.getResponseBody());
    }

    public void sendLogoutRequest(String query) {
        RequestDto requestDto = provider.GETLogout(query, session.getCurrentReferer());
        ResponseDto response = sender.sendGET(requestDto);
        if (!(response.getStatus() == 200)) {
            throw new RuntimeException("Status code error during logout");
        }

        if (!response.getRequestUrl().equals(provider.LOGOUT)) {
            RequestDto logout = provider.GETEmergencyLogout(session.getCurrentReferer());
            sender.sendGET(logout);
        }
    }

    public void setSession(SantanderSession session) {
        this.session = session;
    }
}