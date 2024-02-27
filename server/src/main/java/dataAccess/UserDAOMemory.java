package dataAccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;
import java.util.Map;

public class UserDAOMemory implements UserDAO {
    private Map<String, UserData> users = new HashMap<>();
    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new UserNotFoundException();
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
