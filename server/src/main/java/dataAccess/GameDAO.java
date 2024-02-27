package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    public ArrayList<ChessGame> listGames() throws DataAccessException;
    public int createGame(String gameName) throws DataAccessException;
    public void joinGame(String clientColor, int gameID) throws DataAccessException;

    public void deleteGames() throws DataAccessException;
}
