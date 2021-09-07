package scraper.session.connections;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import static scraper.Header.ACCEPT;
import static scraper.Header.ACCEPT_LANGUAGE;
import static scraper.Header.CACHE_CONTROL;
import static scraper.Header.CONNECTION;
import static scraper.Header.CONTENT_TYPE_URLENCODED;
import static scraper.Header.USER_AGENT;
import static scraper.SantanderAccountsScraper.DASHBOARD_PATH;
import static scraper.SantanderAccountsScraper.HOST;
import static scraper.SantanderAccountsScraper.LOGOUT;
import static scraper.SantanderAccountsScraper.PATH;

public class OkHttpConnectionHandler implements ConnectionHandler {
    private final OkHttpClient client;

    public OkHttpConnectionHandler(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public ResponseDto GETLoginPage() {
        Request request = new Request.Builder().url(HOST + PATH + "/login")
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header(USER_AGENT.name, USER_AGENT.value)
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto GETXmlWithPathForNikPage(String path, String referer) {
        Request request = new Request.Builder().url(HOST + PATH + path)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .header("Wicket-Ajax", "true")
                .header("Wicket-Ajax-BaseURL", referer.substring(referer.indexOf("login?")))
                .header("Wicket-FocusedElementId", "input_nik")
                .header("X-Requested-With", "XMLHttpRequest")
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto POSTNik(String path, String nik, String referer) {
        RequestBody body = new FormBody.Builder()
                .add("nik", nik)
                .add("dp", "")
                .add("loginButton", "1")
                .build();
        Request request = new Request.Builder().url(HOST + PATH + path)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header(CONTENT_TYPE_URLENCODED.name, CONTENT_TYPE_URLENCODED.value)
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .header("Wicket-Ajax", "true")
                .header("Wicket-Ajax-BaseURL", referer.substring(referer.indexOf("login?")))
                .header("Wicket-FocusedElementId", "okBtn2")
                .header("X-Requested-With", "XMLHttpRequest")
                .post(body)
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto GETPasswordPage(String path, String referer) {
        Request request = new Request.Builder().url(HOST + PATH + path)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto GETSendSessionMap(String path, String referer) {
        Request request = new Request.Builder().url(HOST + PATH + path)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .header("Wicket-Ajax", "true")
                .header("Wicket-Ajax-BaseURL", referer.substring(referer.indexOf("crypt.")))
                .header("Wicket-FocusedElementId", "ordinarypin")
                .header("X-Requested-With", "XMLHttpRequest")
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto POSTPassword(String path, String password, String referer) {
        RequestBody body = new FormBody.Builder()
                .add("pinFragment:pin", password)
                .add("loginButton", "Dalej")
                .build();
        Request request = new Request.Builder().url(HOST + PATH + path)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header(CONTENT_TYPE_URLENCODED.name, "application/x-www-form-urlencoded")
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .post(body)
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto POSTToken(String path, String token, String referer) {
        RequestBody body = new FormBody.Builder()
                .add("response", token)
                .add("loginButton", "Dalej")
                .build();
        Request request = new Request.Builder().url(HOST + PATH + path)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header(CONTENT_TYPE_URLENCODED.name, "application/x-www-form-urlencoded")
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .post(body)
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto GETLogout(String path, String referer) {
        Request request = new Request.Builder().url(HOST + DASHBOARD_PATH + path)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto GETProductsPage(String path, String referer) {
        Request request = new Request.Builder().url(HOST + DASHBOARD_PATH + path)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .build();

        return sendHttpRequest(request);
    }

    @Override
    public ResponseDto GETEmergencyLogout(String referer) {
        Request request = new Request.Builder().url(LOGOUT)
                .header(ACCEPT.name, ACCEPT.value)
                .header(ACCEPT_LANGUAGE.name, ACCEPT_LANGUAGE.value)
                .header(CACHE_CONTROL.name, CACHE_CONTROL.value)
                .header(CONNECTION.name, CONNECTION.value)
                .header("Referer", referer)
                .header(USER_AGENT.name, USER_AGENT.value)
                .build();

        return sendHttpRequest(request);
    }

    private ResponseDto sendHttpRequest(Request request) {
        try {
            Response response = client.newCall(request).execute();
            ResponseDto responseDto = ResponseMapper.mapToDto(response);
            response.close();
            return responseDto;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}