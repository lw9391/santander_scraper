package scraper.session;

import scraper.AccountDetails;

import java.util.List;
import java.util.Map;

public class SantanderSession {
    private final RequestHandler requestHandler;
    private String currentReferer;

    private String passwordPath = "";
    private String tokenConfirmationPath = "";
    private String logOutPath = "";
    private String productsPath = "";


    public SantanderSession(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        requestHandler.setSession(this);
    }

    public void sendNikRequest(String nik) {
        String pathForXml = requestHandler.sendLoginPageRequest();
        String pathForNikPage = requestHandler.sendRedirectXmlRequest(pathForXml);
        String pathForPassPage = requestHandler.sendNikRequest(pathForNikPage, nik);

        passwordPath = pathForPassPage;
    }

    public void sendPasswordRequest(String password) {
        Map<PathsNames,String> paths = requestHandler.sendPasswordPageRequest(passwordPath);

        String pathForPasswordRequest = paths.get(PathsNames.PASSWORD);
        String pathForSessionMap = paths.get(PathsNames.SESSION_MAP);

        requestHandler.sendSessionMapRequest(pathForSessionMap);
        pauseExecution();
        tokenConfirmationPath = requestHandler.sendPasswordRequest(pathForPasswordRequest, password);
    }

    private void pauseExecution() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException("This should never happen.",e);
        }
    }

    public void sendTokenRequest(String token) {
        Map<PathsNames,String> paths = requestHandler.sendTokenRequest(tokenConfirmationPath, token);

        logOutPath = paths.get(PathsNames.LOGOUT);
        productsPath = paths.get(PathsNames.PRODUCTS);
    }

    public List<AccountDetails> sendAccountsDetailsRequest() {
        return requestHandler.scrapeAccountsInformation(productsPath);
    }

    public void logOut() {
       requestHandler.sendLogoutRequest(logOutPath);
    }

    void updateReferer(String referer) {
        this.currentReferer = referer;
    }

    String getCurrentReferer() {
        return currentReferer;
    }
}