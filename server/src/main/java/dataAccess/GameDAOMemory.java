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
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null) throw new BadRequestException();
        int id = getUniqueID();
        games.put(id, new GameData(id, null, null, gameName, new ChessGame()));
        return id;
    }

    @Override
    public void joinGame(String username, String clientColor, int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new BadRequestException();
        }
        if (clientColor == null) {
            return; // No color specified, do nothing
        }

        GameData game = games.remove(gameID);
        boolean isWhite = clientColor.equals("WHITE");
        if ((isWhite && game.whiteUsername() != null) || (!isWhite && game.blackUsername() != null)) {
            throw new AlreadyTakenException();
        }
        String newWhiteUsername = isWhite ? username : game.whiteUsername();
        String newBlackUsername = isWhite ? game.blackUsername() : username;
        games.put(gameID, new GameData(gameID, newWhiteUsername, newBlackUsername, game.gameName(), game.game()));
    }


    @Override
    public void deleteGames() {
        games = new HashMap<>();
    }
    @Override
    public void updateGame(int gameID, GameData game) {
        games.put(gameID, game);
    }

    private int getUniqueID() {
        int id = 1;
        while (true) {
            if (!games.containsKey(id)) {
                return id;
            } else id++;
        }
    }
    public int size() {
        return games.size();
    }
}
