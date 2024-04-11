package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
    public static GameData makeMove(String authToken, int gameID, ChessMove move, GameData gameData, GameDAO gameDAO) throws DataAccessException, InvalidMoveException {
        ChessGame game = gameData.game();
        game.makeMove(move);
        GameData updatedGameData = new GameData(gameID,
                gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameDAO.updateGame(gameID, updatedGameData);
        return updatedGameData;
    }
}
