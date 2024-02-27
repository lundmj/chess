package dataAccess;

import dataAccess.Exceptions.AlreadyTakenException;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Exceptions.UnauthorizedException;
import dataAccess.Exceptions.UserNotFoundException;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDAOMemory implements UserDAO {
    private Map<String, UserData> users = new HashMap<>();
    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new UnauthorizedException();
        }
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        if (!users.containsKey(username)) {
            users.put(username, new UserData(username, password, email));
        } else {
            throw new AlreadyTakenException();
        }
    }

    @Override
    public void deleteUsers() {
        users = new HashMap<>();
    }
}
