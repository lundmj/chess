package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import responses.GameIDResponse;

import java.util.ArrayList;

public class GameService {
    public static GameIDResponse createGame(String gameName, String authToken, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        authDAO.getAuth(authToken);
        return new GameIDResponse(gameDAO.createGame(gameName));
    }
    public static ArrayList<GameData> listGames(String authToken, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        authDAO.getAuth(authToken);
        return gameDAO.listGames();
    }
}
