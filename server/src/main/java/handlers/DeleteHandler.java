package handlers;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import service.AdminService;
import spark.Request;
import spark.Response;

public class DeleteHandler{
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public DeleteHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public Object handleRequest(Request req, Response res) throws DataAccessException {
       AdminService.clear(userDAO, authDAO, gameDAO);
       res.status(200);
       return "";
    }
}
