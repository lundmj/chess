package handlers;

import com.google.gson.Gson;
import dataAccess.*;
import model.UserData;
import requests.RegisterRequest;
import responses.ErrorResponse;
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
        try {
            var request = new Gson().fromJson(req.body(), RegisterRequest.class);
            String username = request.username();
            String password = request.password();
            String email = request.email();
            res.status(200);
            return new Gson().toJson(UserService.register(username, password, email, userDAO, authDAO));
        } catch (AlreadyTakenException e) {
            res.status(403);
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        }
    }




}
