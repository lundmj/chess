package dataAccess;

import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    public AuthData createAuth(String username) throws DataAccessException;
    public AuthData getAuth(String authToken) throws DataAccessException;
    public void deleteAuths() throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
}
