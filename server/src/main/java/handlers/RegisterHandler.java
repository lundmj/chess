package handlers;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.DataAccessException;
import requests.RegisterRequest;
import service.UserService;
import spark.Request;
import spark.Response;


public class RegisterHandler {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Object handleRequest(Request req, Response res) throws DataAccessException {
        var request = new Gson().fromJson(req.body(), RegisterRequest.class);
        String username = request.username();
        String password = request.password();
        String email = request.email();
        res.status(200);
        return new Gson().toJson(UserService.register(username, password, email, userDAO, authDAO));
    }
}
