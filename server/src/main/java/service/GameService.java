package service;

import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import responses.GameIDResponse;

import java.util.ArrayList;

public class GameService {
    public static GameIDResponse createGame(String gameName, String authToken, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        authDAO.getAuth(authToken); // just verifying no exception is thrown
        return new GameIDResponse(gameDAO.createGame(gameName));
    }
    public static ArrayList<GameData> listGames(String authToken, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        authDAO.getAuth(authToken); // just verifying no exception is thrown
        return gameDAO.listGames();
    }
    public static void joinGame(String authToken, String playerColor, int gameID, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        String username = authDAO.getAuth(authToken).username();
        gameDAO.joinGame(username, playerColor, gameID);
    }
}
