package handlers;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.UserDAO;
import requests.LoginRequest;
import service.UserService;
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
        String username = request.username();
        String password = request.password();
        res.status(200);
        return new Gson().toJson(UserService.login(username, password, userDAO, authDAO));
    }
}
