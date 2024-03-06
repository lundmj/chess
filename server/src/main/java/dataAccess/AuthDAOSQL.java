package dataAccess;

import dataAccess.Exceptions.DataAccessException;

import java.sql.*;

public class AuthDAOSQL implements AuthDAO {

    private static String createStatement = """
        CREATE TABLE IF NOT EXISTS chess.auths (
          `authToken` varchar(256) NOT NULL,
          `username` varchar(256) NOT NULL,
          PRIMARY KEY (`authToken`)
        )
        """;

    public AuthDAOSQL() throws DataAccessException {
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
