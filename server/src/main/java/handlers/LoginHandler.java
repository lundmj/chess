package handlers;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Object handleRequest(Request req, Response res) throws DataAccessException {
        var request = new Gson().fromJson(req.body(), LoginRequest.class);
    }
}
