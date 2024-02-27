package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthDAOMemory implements AuthDAO {
    private Map<String, AuthData> auths = new HashMap<>();
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        auths.put(authToken, authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (auths.containsKey(authToken)) {
            return auths.get(authToken);
        } else {
            throw new UnauthorizedException();
        }
    }

    @Override
    public void deleteAuths() {
        auths = new HashMap<>();
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (auths.containsKey(authToken)) {
            auths.remove(authToken);
        } else {
            throw new UnauthorizedException();
        }
    }
}
