package dataAccess;


import dataAccess.Exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {

    public UserData getUser(String username) throws DataAccessException;
    public void createUser(String username, String password, String email) throws DataAccessException;
    public void deleteUsers() throws DataAccessException;
}
