package dataAccess;

import dataAccess.Exceptions.DataAccessException;

import java.sql.SQLException;

public class GameDAOSQL implements GameDAO {

    private static String createStatement = """
        CREATE TABLE IF NOT EXISTS chess.games (
          `id` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(256),
          `blackUsername` varchar(256),
          `gameName` varchar(256) NOT NULL,
          `game` longtext NOT NULL,
          PRIMARY KEY (`id`)
        )
        """;

    public GameDAOSQL() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
