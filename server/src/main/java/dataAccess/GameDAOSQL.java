package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.Exceptions.AlreadyTakenException;
import dataAccess.Exceptions.BadRequestException;
import dataAccess.Exceptions.DataAccessException;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class GameDAOSQL implements GameDAO {

    public GameDAOSQL() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String createStatement = """
                    CREATE TABLE IF NOT EXISTS chess.games (
                      `id` int NOT NULL AUTO_INCREMENT,
                      `whiteUsername` varchar(256),
                      `blackUsername` varchar(256),
                      `gameName` varchar(256) NOT NULL,
                      `game` longtext NOT NULL,
                      PRIMARY KEY (`id`)
                    )
                    """;
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        return games;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null) throw new BadRequestException();
        ChessGame game = new ChessGame();
        String gameJson = new Gson().toJson(game);
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, null, null, gameName, gameJson);
    }

    @Override
    public void joinGame(String username, String clientColor, int gameID) throws DataAccessException {
        if (clientColor == null) return;
        boolean isWhite = clientColor.equals("WHITE");
        GameData game = getGame(gameID); // will throw BadRequestException if game doesn't exist
        if ((isWhite && game.whiteUsername() != null) || (!isWhite && game.blackUsername() != null)) {
            throw new AlreadyTakenException();
        }
        String newWhiteUsername = isWhite ? username : game.whiteUsername();
        String newBlackUsername = isWhite ? game.blackUsername() : username;
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=? WHERE id=?";
        executeUpdate(statement, newWhiteUsername, newBlackUsername, gameID);
    }

    private GameData getGame(int id) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        throw new BadRequestException();
    }

    @Override
    public void deleteGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public int size() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {return 0;}
        return 0;
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
