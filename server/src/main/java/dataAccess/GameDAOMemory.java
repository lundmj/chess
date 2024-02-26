package dataAccess;

import chess.ChessGame;

import java.util.ArrayList;

public class GameDAOMemory implements GameDAO {
    @Override
    public ArrayList<ChessGame> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public void joinGame(String clientColor, int gameID) throws DataAccessException {

    }

    @Override
    public void deleteGames() throws DataAccessException {

    }
}
