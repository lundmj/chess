package handlers;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import responses.GameInfo;
import responses.GamesListResponse;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class ListGamesHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public ListGamesHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public Object handleRequest(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        res.status(200);
        return new Gson().toJson(generateResponse(GameService.listGames(authToken, authDAO, gameDAO)));
    }
    private GamesListResponse generateResponse(ArrayList<GameData> games) {
        ArrayList<GameInfo> gameInfos = new ArrayList<>();
        for (GameData game : games) {
            gameInfos.add(new GameInfo(game.id(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return new GamesListResponse(gameInfos);
    }
}
