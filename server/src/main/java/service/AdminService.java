package service;

import dataAccess.DataAccessException;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class AdminService {
    public static void clear(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        userDAO.deleteUsers();
        authDAO.deleteAuths();
        gameDAO.deleteGames();
    }
}