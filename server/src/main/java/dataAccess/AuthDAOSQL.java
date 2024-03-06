package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class AuthDAOSQL implements AuthDAO {

    public AuthDAOSQL() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String createStatement = """
                    CREATE TABLE IF NOT EXISTS chess.auths (
                      `authToken` varchar(256) NOT NULL,
                      `username` varchar(256) NOT NULL,
                      PRIMARY KEY (`authToken`)
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
    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuths() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public int size() {
        return 0;
    }

    private String executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getString(1);
                }

                return "";
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
