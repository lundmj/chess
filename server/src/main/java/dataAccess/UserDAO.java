package dataAccess;


import dataAccess.Exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException;
    void deleteUsers() throws DataAccessException;
    int size();
}
