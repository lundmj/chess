package sqlTests;

import dataAccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;

public class SqlTests {

    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    @BeforeEach
    public void initializeDAOs() {
        userDAO = new UserDAOMemory();
        authDAO = new AuthDAOMemory();
        gameDAO = new GameDAOMemory();
    }

    @Test @DisplayName("Good Get User")
    public void goodGetUser() throws TestException {}
    @Test @DisplayName("Bad Get User")
    public void badGetUser() throws TestException {}
    @Test @DisplayName("Good Create User")
    public void goodCreateUser() throws TestException {}
    @Test @DisplayName("Bad Create User")
    public void badCreateUser() throws TestException {}
    @Test @DisplayName("Good Delete Users")
    public void goodDeleteUsers() throws TestException {}


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
