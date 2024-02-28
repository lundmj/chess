package handlers;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.AlreadyTakenException;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Exceptions.UnauthorizedException;
import requests.JoinRequest;
import responses.ErrorResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public JoinGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public Object handleRequest(Request req, Response res) throws DataAccessException {
        var request = new Gson().fromJson(req.body(), JoinRequest.class);
        String playerColor = request.playerColor();
        int gameID = request.gameID();
        String authToken = req.headers("Authorization");
        res.status(200);
        GameService.joinGame(authToken, playerColor, gameID, authDAO, gameDAO);
        return "{}";
    }
}
