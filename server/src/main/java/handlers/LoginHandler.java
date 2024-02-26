package handlers;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import dataAccess.UserDAO;
import model.UserData;
import requests.LoginRequest;
import responses.ErrorResponse;
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
        try {
            var request = new Gson().fromJson(req.body(), LoginRequest.class);
            String username = request.username();
            String password = request.password();
            res.status(200);
            return new Gson().toJson(UserService.login(username, password, userDAO, authDAO));
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        }
    }
}
