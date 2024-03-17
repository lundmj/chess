package handlers;

import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final AuthDAO authDAO;
    public LogoutHandler(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }
    public Object handleRequest(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        res.status(200);
        UserService.logout(authToken, authDAO);
        return "{}";
    }
}
