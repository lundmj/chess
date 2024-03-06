package serviceTests;

import dataAccess.*;
import dataAccess.Exceptions.AlreadyTakenException;
import dataAccess.Exceptions.BadRequestException;
import dataAccess.Exceptions.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import static org.junit.jupiter.api.Assertions.*;
import responses.GameIDResponse;
import service.AdminService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;

public class ServiceTests {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static String username;
    private static String password;
    private static String email;
    private static String gameName;
    @BeforeEach
    public void initializeDAOs() {
        userDAO = new UserDAOMemory();
        authDAO = new AuthDAOMemory();
        gameDAO = new GameDAOMemory();
    }
    @BeforeAll
    public static void initializeData() {
        username = "mjlund01";
        password = "myPass123";
        email = "fo@pa.com";
        gameName = "MahBabyGame";
    }
    @Test @DisplayName("Good Register")
    public void goodRegister() throws TestException {
        assertDoesNotThrow(() -> {
            AuthData result = UserService.register(username, password, email, userDAO, authDAO);
            assertEquals(result.username(), username);
        });
    }
    @Test @DisplayName("Bad Register")
    public void badRegister() throws TestException {
        assertThrows(AlreadyTakenException.class, () ->  {
            UserService.register(username, password, email, userDAO, authDAO);
            UserService.register(username, password, email, userDAO, authDAO);
        });
        assertThrows(BadRequestException.class, () ->
            UserService.register(null, password, email, userDAO, authDAO));
    }
    @Test @DisplayName("Good Login")
    public void goodLogin() throws TestException {
        assertDoesNotThrow(() -> {
            AuthData initialLoginAuthData = UserService.register(username, password, email, userDAO, authDAO);
            AuthData secondLoginAuthData = UserService.login(username, password, userDAO, authDAO);
            assertEquals(initialLoginAuthData.username(), secondLoginAuthData.username());
            assertNotEquals(initialLoginAuthData.authToken(), secondLoginAuthData.authToken());
        });
    }
    @Test @DisplayName("Bad Login")
    public void badLogin() throws TestException {
        assertThrows(UnauthorizedException.class, () -> {
            UserService.register(username, password, email, userDAO, authDAO);
            UserService.login("otherName", password, userDAO, authDAO);
        });
        assertThrows(UnauthorizedException.class, () ->
            UserService.login(username, "badPass", userDAO, authDAO));
    }
    @Test @DisplayName("Good Logout")
    public void goodLogout() throws TestException {
        assertDoesNotThrow(() -> {
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            UserService.logout(authData.authToken(), authDAO);
        });
    }
    @Test @DisplayName("Bad Logout")
    public void badLogout() throws TestException {
        assertThrows(UnauthorizedException.class, () -> {
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            UserService.logout(authData.authToken(), authDAO);
            UserService.logout(authData.authToken(), authDAO);
        });
        assertThrows(UnauthorizedException.class, () -> UserService.logout("FakeToken283094023894", authDAO));
    }
    @Test @DisplayName("Good Create Game")
    public void goodCreate() throws TestException {
        assertDoesNotThrow(() -> {
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            GameIDResponse gameIDResponse = GameService.createGame(gameName, authData.authToken(), authDAO, gameDAO);
            assertEquals(gameIDResponse.gameID(), 1);
            gameIDResponse = GameService.createGame(gameName, authData.authToken(), authDAO, gameDAO);
            assertEquals(gameIDResponse.gameID(), 2);
        });
    }
    @Test @DisplayName("Bad Create Game")
    public void badCreate() throws TestException {
        assertThrows(UnauthorizedException.class, () ->
            GameService.createGame(gameName, "token", authDAO, gameDAO));
        assertThrows(BadRequestException.class, () -> {
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            GameService.createGame(null, authData.authToken(), authDAO, gameDAO);
        });
    }
    @Test @DisplayName("Good List Games")
    public void goodList() throws TestException {
        assertDoesNotThrow(() -> {
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            assertEquals(GameService.listGames(authData.authToken(), authDAO, gameDAO), new ArrayList<>());
            GameService.createGame("game1", authData.authToken(), authDAO, gameDAO);
            GameService.createGame("game2", authData.authToken(), authDAO, gameDAO);
            GameService.createGame("game3", authData.authToken(), authDAO, gameDAO);
            assertEquals(GameService.listGames(authData.authToken(), authDAO, gameDAO).size(), 3);
        });
    }
    @Test @DisplayName("Bad List Games")
    public void badList() throws TestException {
        assertThrows(UnauthorizedException.class, () -> GameService.listGames("qwerty", authDAO, gameDAO));
    }
    @Test @DisplayName("Good Join Game")
    public void goodJoin() throws TestException {
        assertDoesNotThrow(() -> {
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            GameIDResponse gameIDResponse = GameService.createGame(gameName, authData.authToken(), authDAO, gameDAO);
            GameService.joinGame(authData.authToken(), "WHITE", gameIDResponse.gameID(), authDAO, gameDAO);

            AuthData authData2 = UserService.register("other", "other", email, userDAO, authDAO);
            GameIDResponse gameIDResponse2 = GameService.createGame(gameName, authData2.authToken(), authDAO, gameDAO);
            GameService.joinGame(authData2.authToken(), "BLACK", gameIDResponse2.gameID(), authDAO, gameDAO);
        });
    }
    @Test @DisplayName("Bad Join Game")
    public void badJoin() throws TestException {
        assertThrows(UnauthorizedException.class, () ->
            GameService.joinGame("fakeAuthToken81203", "WHITE", 1, authDAO, gameDAO));
        assertThrows(BadRequestException.class, () -> {
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            GameService.createGame(gameName, authData.authToken(), authDAO, gameDAO);
            GameService.joinGame(authData.authToken(), "WHITE", 100, authDAO, gameDAO);
        });
        assertThrows(AlreadyTakenException.class, () -> {
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            GameIDResponse gameIDResponse = GameService.createGame(gameName, authData.authToken(), authDAO, gameDAO);
            GameService.joinGame(authData.authToken(), "WHITE", 100, authDAO, gameDAO);

            AuthData authData2 = UserService.register("other", "other", email, userDAO, authDAO);
            GameService.joinGame(authData2.authToken(), "WHITE", gameIDResponse.gameID(), authDAO, gameDAO);
        });
    }
    @Test @DisplayName("Good Clear")
    public void clear() throws TestException {
        assertDoesNotThrow(() -> {
            // make a **** ton of stuff in the DAOs, then see if it is gone once you clear
            AuthData authData = UserService.register(username, password, email, userDAO, authDAO);
            UserService.register("a", password, email, userDAO, authDAO);
            UserService.register("b", password, email, userDAO, authDAO);
            UserService.register("c", password, email, userDAO, authDAO);
            GameService.createGame(gameName, authData.authToken(), authDAO, gameDAO);
            GameService.createGame("e", authData.authToken(), authDAO, gameDAO);
            GameService.createGame("f", authData.authToken(), authDAO, gameDAO);
            GameService.createGame("g", authData.authToken(), authDAO, gameDAO);
            GameService.joinGame(authData.authToken(), "BLACK", 1, authDAO, gameDAO);

            AdminService.clear(userDAO, authDAO, gameDAO);

            assertEquals(userDAO.size(), 0);
            assertEquals(gameDAO.size(), 0);
            assertEquals(authDAO.size(), 0);
        });
    }
}
