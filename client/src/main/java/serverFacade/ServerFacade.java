package serverFacade;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import ui.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String url;
    public ServerFacade(String url) {
        this.url = url;
    }
    public AuthData register(UserData user) throws ResponseException {
        return makeRequest("POST", "/user", null, user, AuthData.class);
    }

    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(this.url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);

            writeHeader(authToken, http);
            print("Made it past write header");
            http.connect();
            print("Connected");
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            String jsonReq = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(jsonReq.getBytes());
            }
        }
    }
    private static void writeHeader(String authToken, HttpURLConnection http) {
        if (authToken != null) {
            http.addRequestProperty("Authorization", authToken);
        }
    }
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (status / 100 != 2) {
            throw switch (status) {
                case 400 -> new ResponseException(status, "Error: bad request");
                case 401 -> new ResponseException(status, "Error: unauthorized");
                case 403 -> new ResponseException(status, "Error: already taken");
                default -> new ResponseException(status, "Error");
            };
        }
    }
    private void print(Object item) {
        System.out.println(item);
    }
}
