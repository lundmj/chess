package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class GameDAOMemory implements GameDAO {

    private Map<Integer, GameData> games = new HashMap<>();
    @Override
    public ArrayList<ChessGame> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public int createGame(String gameName) {
        int id = getUniqueID();
        games.put(id, new GameData(id, null, null, gameName, new ChessGame()));
        return id;
    }

    @Override
    public void joinGame(String clientColor, int gameID) throws DataAccessException {

    }

    @Override
    public void deleteGames() {
        games = new HashMap<>();
    }

    private int getUniqueID() {
        int id = 1;
        while (true) {
            if (!games.containsKey(id)) {
                return id;
            } else id++;
        }
    }
}
