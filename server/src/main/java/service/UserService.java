package service;

import dataAccess.*;
import dataAccess.Exceptions.BadRequestException;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;

public class UserService {
    public static AuthData register(String username, String password, String email, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        verifyFieldsNotEmpty(username, password, email);
        userDAO.createUser(username, password, email);
        return authDAO.createAuth(username);
    }
    public static AuthData login(String username, String password, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        UserData user = userDAO.getUser(username);
        validateUser(user, password);
        return authDAO.createAuth(username);
    }
    public static void logout(String authToken, AuthDAO authDAO) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }


    // private helper methods
    private static void verifyFieldsNotEmpty(String username, String password, String email) throws BadRequestException {
        if (username == null || password == null || email == null) throw new BadRequestException();
    }
    private static void validateUser(UserData user, String password) throws UnauthorizedException {
        if (!user.password().equals(password)) throw new UnauthorizedException();
    }
}