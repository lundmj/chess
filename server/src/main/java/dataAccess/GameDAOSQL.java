package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

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

    }

    @Override
    public int size() {
        return 0;
    }
}
