package dataAccess;

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
        return null;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public void joinGame(String username, String clientColor, int gameID) throws DataAccessException {

    }

    @Override
    public void deleteGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public int size() {
        return 0;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        default -> ps.setNull(i + 1, NULL);
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
