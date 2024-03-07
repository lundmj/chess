package dataAccessTests;

import dataAccess.*;
import dataAccess.Exceptions.AlreadyTakenException;
import dataAccess.Exceptions.BadRequestException;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static String username;
    private static String password;
    private static String email;
    private static String gameName;

    @BeforeAll
    public static void initializeDAOs() {
        try {
            userDAO = new UserDAOSQL();
            authDAO = new AuthDAOSQL();
            gameDAO = new GameDAOSQL();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void initializeData() {
        username = "mjlund01";
        password = "myPass123";
        email = "fo@pa.com";
        gameName = "MahBabyGame";
    }

    @BeforeEach
    public void clearData() { // always clear out data before every test
        assertDoesNotThrow(() -> {
            userDAO.deleteUsers();
            authDAO.deleteAuths();
            gameDAO.deleteGames();
        });
    }

    @Test @DisplayName("Good Get User")
    public void goodGetUser() throws TestException {
        assertDoesNotThrow(() -> {
            userDAO.createUser(username, password, email);
            userDAO.createUser("other", "newpass", "yada@yada.yada");
            String returnedUsername = userDAO.getUser(username).username();
            assertEquals(username, returnedUsername);
        });
    }
    @Test @DisplayName("Bad Get User")
    public void badGetUser() throws TestException {
        assertThrows(UnauthorizedException.class, () -> userDAO.getUser(username));
    }
    @Test @DisplayName("Good Create User")
    public void goodCreateUser() throws TestException {
        assertDoesNotThrow(() -> {
            assertEquals(userDAO.size(), 0);
            userDAO.createUser(username, password, email);
            assertEquals(userDAO.size(), 1); //assert it got bigger
        });
    }
    @Test @DisplayName("Bad Create User")
    public void badCreateUser() throws TestException {
        assertThrows(AlreadyTakenException.class, () -> {
            userDAO.createUser(username, password, email);
            userDAO.createUser(username, password, email);
        });
    }
    @Test @DisplayName("Good Delete Users")
    public void goodDeleteUsers() throws TestException {
        assertDoesNotThrow(() -> {
            userDAO.createUser(username, password, email);
            userDAO.createUser("new", password, email);
            userDAO.createUser("other", password, email);
            assertEquals(userDAO.size(), 3);
            userDAO.deleteUsers();
            assertEquals(userDAO.size(), 0);
        });
    }


    @Test @DisplayName("Good Create Auth")
    public void goodCreateAuth() throws TestException {
        assertDoesNotThrow(() -> {
            assertEquals(authDAO.size(), 0);
            authDAO.createAuth(username);
            assertEquals(authDAO.size(), 1);
        });
    }
    @Test @DisplayName("Bad Create Auth")
    public void badCreateAuth() throws TestException {
        assertThrows(BadRequestException.class, () -> authDAO.createAuth(null));
    }
    @Test @DisplayName("Good Get Auth")
    public void goodGetAuth() throws TestException {
        assertDoesNotThrow(() -> {
            AuthData authData = authDAO.createAuth(username);
            String returnedUsername = authDAO.getAuth(authData.authToken()).username();
            assertEquals(returnedUsername, username);
        });
    }
    @Test @DisplayName("Bad Get Auth")
    public void badGetAuth() throws TestException {
        assertThrows(UnauthorizedException.class, () -> {
            authDAO.createAuth(username);
            authDAO.getAuth("fake-auth38921389");
        });
    }
    @Test @DisplayName("Good Delete Many Auths")
    public void goodDeleteAuths() throws TestException {
        assertDoesNotThrow(() -> {
            assertEquals(authDAO.size(), 0);
            authDAO.createAuth(username);
            authDAO.createAuth("otheruser");
            authDAO.createAuth("anotherone");
            assertEquals(authDAO.size(), 3);
            authDAO.deleteAuths();
            assertEquals(authDAO.size(), 0);
        });

    }
    @Test @DisplayName("Good Delete One Auth")
    public void goodDeleteAuth() throws TestException {
        assertDoesNotThrow(() -> {
            AuthData data1 = authDAO.createAuth(username);
            AuthData data2 = authDAO.createAuth("no.2");
            authDAO.deleteAuth(data1.authToken());
            assertEquals(authDAO.size(), 1);
            assertThrows(UnauthorizedException.class, () -> authDAO.getAuth(data1.authToken()));
            assertDoesNotThrow(() -> authDAO.getAuth(data2.authToken()));
        });
    }

    @Test @DisplayName("Good Create Game")
    public void goodCreateGame() throws TestException {
        assertDoesNotThrow(() -> {
            assertEquals(gameDAO.size(), 0);
            gameDAO.createGame(gameName);
            assertEquals(gameDAO.size(), 1);
        });
    }
    @Test @DisplayName("Bad Create Game")
    public void badCreateGame() throws TestException {
        assertThrows(BadRequestException.class, () -> gameDAO.createGame(null));
    }
    @Test @DisplayName("Good List Games")
    public void goodListGames() throws TestException {
        assertDoesNotThrow(() -> {
            int id = gameDAO.createGame(gameName);
            int id2 = gameDAO.createGame("otherGame");
            int id3 = gameDAO.createGame("thirdGame");
            ArrayList<GameData> games = gameDAO.listGames();
            assertEquals(games.get(0).id(), id);
            assertEquals(games.get(0).gameName(), gameName);
            assertEquals(games.get(1).id(), id2);
            assertEquals(games.get(1).gameName(), "otherGame");
            assertEquals(games.get(2).id(), id3);
            assertEquals(games.get(2).gameName(), "thirdGame");
        });
    }
    @Test @DisplayName("Bad List Games")
    public void badListGames() throws TestException {

    }
    @Test @DisplayName("Good Join")
    public void goodJoin() throws TestException {
        assertDoesNotThrow(() -> {
            int id = gameDAO.createGame(gameName);
            gameDAO.joinGame(username, "WHITE", id);
            GameData game = gameDAO.listGames().getFirst();
            assertEquals(game.gameName(), gameName);
            assertEquals(game.id(), id);
            assertEquals(game.whiteUsername(), username);
            assertNull(game.blackUsername());
        });
    }
    @Test @DisplayName("Bad Join")
    public void badJoin() throws TestException {
        assertThrows(BadRequestException.class, () -> gameDAO.joinGame(username, "WHITE", 50));
        assertThrows(AlreadyTakenException.class, () -> {
            int id = gameDAO.createGame(gameName);
            gameDAO.joinGame(username, "BLACK", id);
            gameDAO.joinGame("other", "BLACK", id);
        });
    }
    @Test @DisplayName("Good Delete Games")
    public void goodDeleteGames() throws TestException {
        assertDoesNotThrow(() -> {
            gameDAO.createGame(gameName);
            gameDAO.createGame(gameName);
            gameDAO.createGame(gameName);
            gameDAO.createGame(gameName);
            gameDAO.createGame(gameName);
            gameDAO.createGame(gameName);
            assertEquals(gameDAO.size(), 6);
            gameDAO.deleteGames();
            assertEquals(gameDAO.size(), 0);
        });
    }

}
