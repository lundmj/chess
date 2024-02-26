package service;

import dataAccess.*;
import dataAccess.BadRequestException;
import model.AuthData;
import model.UserData;

public class UserService {
    public static AuthData register(UserData user, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        verifyFieldsNotEmpty(user);
        userDAO.createUser(user.username(), user.password(), user.email());
        return authDAO.createAuth(user.username());
    }
    public AuthData login(UserData user) { return null; }
    public void logout(UserData user) {}

    private static void verifyFieldsNotEmpty(UserData user) throws BadRequestException {
        if (user.username() == null
         || user.password() == null
         || user.email() == null) {
            throw new BadRequestException();
        }
    }
}