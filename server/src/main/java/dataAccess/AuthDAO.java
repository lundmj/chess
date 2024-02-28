package dataAccess;

import dataAccess.Exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuths() throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    int size();
}
