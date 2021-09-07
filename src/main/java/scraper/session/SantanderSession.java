package scraper.session;

import scraper.AccountDetails;

import java.util.List;
import java.util.Map;

import static scraper.session.SessionState.FULLY_LOGGED;
import static scraper.session.SessionState.NIK_ACCEPTED;
import static scraper.session.SessionState.NONE;
import static scraper.session.SessionState.PASSWORD_ACCEPTED;

public class SantanderSession {
    private final RequestHandler requestHandler;
    private SessionState state;
    private String currentReferer;

    private String passwordPath = "";
    private String tokenConfirmationPath = "";
    private String logOutPath = "";
    private String productsPath = "";


    public SantanderSession(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        requestHandler.setSession(this);
        this.state = NONE;
    }

    public void sendNikRequest(String nik) {
        if (state != NONE) {
            throw new IllegalStateException("Wrong order of requests.");
        }
        String pathForXml = requestHandler.sendLoginPageRequest();
        String pathForNikPage = requestHandler.sendRedirectXmlRequest(pathForXml);
        String pathForPassPage = requestHandler.sendNikRequest(pathForNikPage, nik);

        passwordPath = pathForPassPage;
        state = NIK_ACCEPTED;
    }

    public void sendPasswordRequest(String password) {
        if (state != NIK_ACCEPTED) {
            throw new IllegalStateException("Wrong order of requests.");
        }
        Map<PathsNames,String> paths = requestHandler.sendPasswordPageRequest(passwordPath);

        String pathForPasswordRequest = paths.get(PathsNames.PASSWORD);
        String pathForSessionMap = paths.get(PathsNames.SESSION_MAP);

        requestHandler.sendSessionMapRequest(pathForSessionMap);
        pauseExecution();
        tokenConfirmationPath = requestHandler.sendPasswordRequest(pathForPasswordRequest, password);
        state = PASSWORD_ACCEPTED;
    }

    private void pauseExecution() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException("This should never happen.",e);
        }
    }

    public void sendTokenRequest(String token) {
        if (state != PASSWORD_ACCEPTED || tokenConfirmationPath.isEmpty()) {
            throw new IllegalStateException("Wrong order of requests.");
        }
        Map<PathsNames,String> paths = requestHandler.sendTokenRequest(tokenConfirmationPath, token);

        logOutPath = paths.get(PathsNames.LOGOUT);
        productsPath = paths.get(PathsNames.PRODUCTS);
        state = FULLY_LOGGED;
    }

    public List<AccountDetails> sendAccountsDetailsRequest() {
        if (state != FULLY_LOGGED || productsPath.isEmpty()) {
            throw new IllegalStateException("Wrong order of requests.");
        }
        return requestHandler.scrapAccountsInformation(productsPath);
    }

    public void logOut() {
        if (state != FULLY_LOGGED) {
            throw new IllegalStateException("Wrong order of requests.");
        }
       requestHandler.sendLogoutRequest(logOutPath);
    }

    void updateReferer(String referer) {
        this.currentReferer = referer;
    }

    String getCurrentReferer() {
        return currentReferer;
    }
}