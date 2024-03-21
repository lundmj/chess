package clientTests;

import model.AuthData;
import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.GameIDResponse;
import server.Server;
import serverFacade.ServerFacade;
import ui.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private final RegisterRequest registerRequest = new RegisterRequest("mjlund01", "password", "my@email.com");
    private final LoginRequest loginRequest = new LoginRequest("mjlund01", "password");
    private final CreateGameRequest createGameRequest = new CreateGameRequest("game");
    private final JoinRequest joinRequest = new JoinRequest("WHITE", 1);

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clear() {
        assertDoesNotThrow(() -> facade.clear());
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test @DisplayName("Good Register")
    public void goodRegister() {
        assertDoesNotThrow(() -> {
            AuthData result = facade.register(registerRequest);
            assertEquals(result.username(), registerRequest.username());
        });
    }

    @Test @DisplayName("Bad Register")
    public void badRegister() {
        assertThrows(ResponseException.class, () -> {
            facade.register(registerRequest);
            facade.register(registerRequest); // Attempt to register twice with the same credentials
        });
    }

    @Test @DisplayName("Good Login")
    public void goodLogin() {
        assertDoesNotThrow(() -> {
            AuthData result = facade.register(registerRequest);
            facade.logout(result.authToken());
            result = facade.login(loginRequest);
            assertEquals(result.username(), loginRequest.username());
        });
    }

    @Test @DisplayName("Bad Login")
    public void badLogin() {
        assertThrows(ResponseException.class, () -> facade.login(loginRequest));
    }

    @Test @DisplayName("Good Logout")
    public void goodLogout() {
        assertDoesNotThrow(() -> {
            AuthData result = facade.register(registerRequest);
            facade.logout(result.authToken());
        });
    }

    @Test @DisplayName("Bad Logout")
    public void badLogout() {
        assertThrows(ResponseException.class, () -> facade.logout("not yet signed in"));
    }

    @Test @DisplayName("Good Create")
    public void goodCreate() {
        assertDoesNotThrow(() -> {
            AuthData result = facade.register(registerRequest);
            GameIDResponse game = facade.createGame(createGameRequest, result.authToken());
            assertEquals(1, game.gameID());
        });
    }

    @Test @DisplayName("Bad Create")
    public void badCreate() {
        assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest, "bad auth token"));
    }

    @Test @DisplayName("Good List")
    public void goodList() {
        assertDoesNotThrow(() -> {
            AuthData result = facade.register(registerRequest);
            facade.listGames(result.authToken());
            facade.createGame(createGameRequest, result.authToken());
            facade.createGame(createGameRequest, result.authToken());
            facade.createGame(createGameRequest, result.authToken());
            facade.createGame(createGameRequest, result.authToken());
            facade.createGame(createGameRequest, result.authToken());
            facade.listGames(result.authToken());
        });
    }

    @Test @DisplayName("Bad List")
    public void badList() {
        assertThrows(ResponseException.class, () -> facade.listGames("bad auth token"));
    }

    @Test @DisplayName("Good Join")
    public void goodJoin() {
        assertDoesNotThrow(() -> {
            AuthData result = facade.register(registerRequest);
            facade.createGame(createGameRequest, result.authToken());
            facade.listGames(result.authToken());
            facade.joinGame(joinRequest, result.authToken());
        });
    }

    @Test @DisplayName("Bad Join")
    public void badJoin() {
        assertThrows(ResponseException.class, () -> facade.joinGame(joinRequest, "bad auth"));
    }
}
