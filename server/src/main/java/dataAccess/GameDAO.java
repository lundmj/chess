package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    ArrayList<GameData> listGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    void joinGame(String username, String clientColor, int gameID) throws DataAccessException;

    void deleteGames() throws DataAccessException;
    int size();
}
