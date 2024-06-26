package dataAccess;

import dataAccess.Exceptions.AlreadyTakenException;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Exceptions.UnauthorizedException;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class UserDAOSQL implements UserDAO {


    public UserDAOSQL() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            String createStatement = """
                    CREATE TABLE IF NOT EXISTS users (
                      `username` varchar(256) NOT NULL,
                      `password` varchar(256) NOT NULL,
                      `email` varchar(256) NOT NULL,
                      PRIMARY KEY (`username`)
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
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        throw new UnauthorizedException();
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        try {
            getUser(username);
        } catch (UnauthorizedException e) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            ExecuteUpdate.execute(statement, username, password, email);
            return;
        }
        throw new AlreadyTakenException();
    }

    @Override
    public void deleteUsers() throws DataAccessException {
        var statement = "TRUNCATE users";
        ExecuteUpdate.execute(statement);
    }

    @Override
    public int size() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM users";
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

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }
}
