package sqlTests;

import dataAccess.*;
import dataAccess.Exceptions.AlreadyTakenException;
import dataAccess.Exceptions.BadRequestException;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.Exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import static org.junit.jupiter.api.Assertions.*;

public class SqlTests {

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
        assertThrows(UnauthorizedException.class, () -> {
            userDAO.getUser(username);
        });
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
    public void goodCreateAuth() throws TestException {}
    @Test @DisplayName("Bad Create Auth")
    public void badCreateAuth() throws TestException {}
    @Test @DisplayName("Good Get Auth")
    public void goodGetAuth() throws TestException {}
    @Test @DisplayName("Bad Get Auth")
    public void badGetAuth() throws TestException {}
    @Test @DisplayName("Good Delete Auths")
    public void goodDeleteAuths() throws TestException {}
    @Test @DisplayName("Good Delete Auth")
    public void goodDeleteAuth() throws TestException {}


    @Test @DisplayName("Good List Games")
    public void goodListGames() throws TestException {}
    @Test @DisplayName("Bad List Games")
    public void badListGames() throws TestException {}
    @Test @DisplayName("Good Create Game")
    public void goodCreateGame() throws TestException {}
    @Test @DisplayName("Bad Create Game")
    public void badCreateGame() throws TestException {}
    @Test @DisplayName("Good Join")
    public void goodJoin() throws TestException {}
    @Test @DisplayName("Bad Join")
    public void badJoin() throws TestException {}
    @Test @DisplayName("Good Delete Games")
    public void goodDeleteGames() throws TestException {}

}