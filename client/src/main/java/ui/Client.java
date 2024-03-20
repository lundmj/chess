package ui;

import model.AuthData;
import requests.CreateGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.GameInfo;
import serverFacade.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import static ui.EscapeSequences.*;

public class Client {
    private State state = State.SIGNEDOUT;
    private final String url;
    private final ServerFacade server;
    private String authToken = null;
    private ArrayList<GameInfo> gamesList;
    public Client(String url) {
        this.url = url;
        this.server = new ServerFacade(url);
    }
    public String eval(String input) throws ResponseException {
        String[] tokens = input.toLowerCase().split(" ");
        String command = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (command) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "logout" -> logout();
            case "create" -> createGame(params);
            case "list" -> list();
            case "clearall" -> clear();
            case "quit", "exit" -> quit();
            default -> help();
        };

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
        if (params.length != 0) {
            String name = String.join("_", params);
            server.createGame(new CreateGameRequest(name), authToken);
            return "Successfully created game: " + name;
        }
        throw new ResponseException(400, "Expected: create <game name>");
    }

    private String list() throws ResponseException {
        assertSignedIn();
        gamesList = server.listGames(authToken).games();

        if (gamesList.isEmpty()) return "⚠ No games available\nTo create a game, use: create <game name>";

        for (int i = 0; i < gamesList.size(); i++) {
            GameInfo game = gamesList.get(i);
            String gameName = game.gameName();
            String white = game.whiteUsername();
            String black = game.blackUsername();
            System.out.print(i+1);
            System.out.println(white(". " + gameName));
            if (black == null && white == null) {
                System.out.println(white("No players"));
            } else {
                if (white != null) System.out.println(white(white));
                if (black != null) System.out.println(white(black));
            }
            System.out.println();
        }
        return "To join a game, use: join <game name>";
    }

    private String quit() throws ResponseException {
        if (state == State.SIGNEDIN) logout();
        return "quit";
    }
    public String help() {
        if (state == State.SIGNEDOUT)
            return blue("register <username> <password> <email>") + white(" - create account\n")
                    + blue("login <username> <password>") + white(" - to play\n")
                    + blue("quit\n")
                    + blue("help") + white(" - see this menu");
        else
            return blue("create <game name>\n")
                    + blue("list") + white(" - show available games\n")
                    + blue("join <id> [WHITE|BLACK]") + white(" - join game as a player\n")
                    + blue("observe <id>") + white(" - join game as an observer\n")
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
