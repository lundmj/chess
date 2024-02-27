package handlers;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UnauthorizedException;
import requests.CreateGameRequest;
import responses.ErrorResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public CreateGameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public Object handleRequest(Request req, Response res) throws DataAccessException {
        try {
            var request = new Gson().fromJson(req.body(), CreateGameRequest.class);
            String gameName = request.gameName();
            String authToken = req.headers("Authorization");
            res.status(200);
            return new Gson().toJson(GameService.createGame(gameName, authToken, authDAO, gameDAO));
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        }
    }
}
