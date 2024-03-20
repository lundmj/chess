package ui;

import model.AuthData;
import model.UserData;
import requests.CreateGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.GameIDResponse;
import serverFacade.ServerFacade;

import java.util.Arrays;
import static ui.EscapeSequences.*;

public class Client {
    private State state = State.SIGNEDOUT;
    private final String url;
    private final ServerFacade server;
    private String authToken = null;
    public Client(String url) {
        this.url = url;
        this.server = new ServerFacade(url);
    }
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (command) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "clearall" -> clear();
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }


    // vv COMMAND FUNCTIONS vv //
    private String clear() throws ResponseException {
        assertSignedOut();
        server.clear();
        return "Cleared all data from database";
    }
    private String register(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 3) {
            AuthData auth = server.register(new RegisterRequest(params[0], params[1], params[2]));
            authToken = auth.authToken();
            state = State.SIGNEDIN;
            return "Successfully registered: " + auth.username();
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }
    private String login(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 2) {
            AuthData auth = server.login(new LoginRequest(params[0], params[1]));
            authToken = auth.authToken();
            state = State.SIGNEDIN;
            return "Successfully logged in: " + auth.username();
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        server.logout(authToken);
        authToken = null;
        state = State.SIGNEDOUT;
        return "Logged out";
    }
    private String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            String name = params[0];
            server.createGame(new CreateGameRequest(name), authToken);
            return "Successfully created game: " + name;
        }
        throw new ResponseException(400, "Expected: create <game name>");
    }

    private String quit() throws ResponseException {
        if (state == State.SIGNEDIN) logout();
        return "quit";
    }
    public String help() {
        if (state == State.SIGNEDOUT)
            return blue("register <USERNAME> <PASSWORD> <EMAIL>") + white(" - create account\n")
                 + blue("login <USERNAME> <PASSWORD>") + white(" - to play\n")
                 + blue("quit\n")
                 + blue("help") + white(" - see this menu");
        else
            return blue("create <GAME NAME>\n")
                 + blue("list") + white(" - show available games\n")
                 + blue("join <ID> [WHITE|BLACK]") + white(" - join game as a player\n")
                 + blue("observe <ID>") + white(" - join game as an observer\n")
                 + blue("logout\n")
                 + blue("quit\n")
                 + blue("help") + white(" - see this menu");
    }

    private String blue(String string) {
        return SET_TEXT_COLOR_BLUE + string;
    }
    private String white(String string) {
        return SET_TEXT_COLOR_WHITE + string;
    }
    private String green(String string) {
        return SET_TEXT_COLOR_GREEN + string;
    }
    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
    private void assertSignedOut() throws ResponseException {
        if (state == State.SIGNEDIN) {
            throw new ResponseException(400, "You must be signed out");
        }
    }
}
