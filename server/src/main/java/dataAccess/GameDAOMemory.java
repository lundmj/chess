package dataAccess;

import chess.ChessGame;
import dataAccess.Exceptions.AlreadyTakenException;
import dataAccess.Exceptions.BadRequestException;
import dataAccess.Exceptions.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameDAOMemory implements GameDAO {

    private Map<Integer, GameData> games = new HashMap<>();
    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public int createGame(String gameName) {
        int id = getUniqueID();
        games.put(id, new GameData(id, null, null, gameName, new ChessGame()));
        return id;
    }

    @Override
    public void joinGame(String username, String clientColor, int gameID) throws DataAccessException {
        if (!games.containsKey(gameID))
            throw new BadRequestException();
        GameData game = games.remove(gameID);
        boolean isWhite = clientColor.equals("WHITE");
        if ((isWhite && game.whiteUsername() != null)  ||  (!isWhite && game.blackUsername() != null)) {
            throw new AlreadyTakenException();
        }
        if (isWhite)
            games.put(gameID, new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game()));
        else
            games.put(gameID, new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game()));

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
