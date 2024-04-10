package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import requests.CreateGameRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.GameInfo;
import serverFacade.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;

import java.util.ArrayList;
import java.util.Arrays;
import static chess.EscapeSequences.*;

public class Client implements NotificationHandler {
    private static class Prompts {
        String quit = "quit";
        String help = "help";
        String register = "register <username> <password> <email>";
        String login = "login <username> <password>";
        String create = "create <game name>";
        String list = "list";
        String join = "join <id> [WHITE|BLACK]";
        String observe = "observe <id>";
        String logout = "logout";
    }

    private final Prompts prompts = new Prompts();
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private WebSocketFacade ws;
    private final String url;
    private String authToken = null;
    private ArrayList<GameInfo> gamesList;
    private ChessGame.TeamColor color = null;

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
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "clearall" -> clear();
            case "quit", "exit" -> quit();
            default -> help();
        };

    }


    // vv COMMAND FUNCTIONS vv //
    private String clear() throws ResponseException {
        if (state == State.SIGNEDIN) logout();
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
        throw new ResponseException(400, "Expected: " + prompts.register);
    }

    private String login(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 2) {
            AuthData auth = server.login(new LoginRequest(params[0], params[1]));
            authToken = auth.authToken();
            state = State.SIGNEDIN;
            return "Successfully logged in: " + auth.username();
        }
        throw new ResponseException(400, "Expected: " + prompts.login);
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
        throw new ResponseException(400, "Expected: " + prompts.create);
    }

    private String list() throws ResponseException {
        assertSignedIn();
        gamesList = server.listGames(authToken).games();

        if (gamesList.isEmpty()) {
            System.out.println(SET_TEXT_COLOR_YELLOW + "  ⚠ No games available");
            return "To create a game, use: " + prompts.create;
        }

        for (int i = 0; i < gamesList.size(); i++) {
            GameInfo game = gamesList.get(i);
            String gameName = game.gameName();
            String white = game.whiteUsername();
            String black = game.blackUsername();
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(i + 1);
            System.out.println(". " + gameName);
            if (black == null && white == null) {
                System.out.println(white("  No players"));
            } else {
                if (white != null) System.out.println("  " + white);
                if (black != null) System.out.println("  " + black);
            }
            System.out.println();
        }
        return String.format("To join a game, use: %s\n  To observe a game, use: %s", prompts.join, prompts.observe);
    }
    private String join(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            String color = params[1].toUpperCase();
            int gameID = getRealID(params[0]);
            server.joinGame(new JoinRequest(color, gameID), authToken);
            this.color = (color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
            new WebSocketFacade(url, this).joinPlayer(authToken, gameID, this.color);
            return "Successfully joined team: " + color;
        }
        throw new ResponseException(400, "Expected: " + prompts.join);
    }
    private String observe(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            int gameID = getRealID(params[0]);
            server.joinGame(new JoinRequest(null, gameID), authToken);
            new WebSocketFacade(url, this).joinObserver(authToken, gameID);
            return "Successfully observing game";
        }
        throw new ResponseException(400, "Expected: " + prompts.observe);
    }

    private String quit() throws ResponseException {
        if (state == State.SIGNEDIN) logout();
        return "quit";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            System.out.println(
                blue(prompts.register) + white(" - create account\n")
              + blue(prompts.login) + white(" - to play\n")
              + blue(prompts.quit + "\n")
              + blue(prompts.help) + white(" - see this menu")
            );
        } else {
            System.out.println(
                blue(prompts.create + "\n")
              + blue(prompts.list) + white(" - show available games\n")
              + blue(prompts.join) + white(" - join game as a player\n")
              + blue(prompts.observe) + white(" - join game as an observer\n")
              + blue(prompts.logout + "\n")
              + blue(prompts.quit + "\n")
              + blue(prompts.help) + white(" - see this menu")
            );
        }
        return "Enter any of the above commands to continue...";
    }


    private String blue(String string) {
        return SET_TEXT_COLOR_BLUE + string;
    }

    private String white(String string) {
        return SET_TEXT_COLOR_WHITE + string;
    }
    private void assertSignedIn() throws ResponseException {
        if (state != State.SIGNEDIN) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state != State.SIGNEDOUT) {
            throw new ResponseException(400, "You must be signed out");
        }
    }

    private int getRealID(String providedID) throws ResponseException {
        try {
            int idInList = Integer.parseInt(providedID) - 1; // convert 1 based to 0 based
            return gamesList.get(idInList).gameID();
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Expected an integer for id, to join a game, use: %s" + prompts.join);
        }
    }

    public void notify(Notification message) {
        System.out.print(fixWSResponseWithPrompt(SET_TEXT_COLOR_MAGENTA + message.getMessage()));
    }
    public void loadGame(LoadGame message) {
        String gameJson = message.getGameJson();
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        System.out.print(fixWSResponseWithPrompt(switch (color) {
            case WHITE -> game.getBoard().getWhitePerspective();
            case BLACK -> game.getBoard().getBlackPerspective();
        }));
    }
    public void displayError(Error message) {

    }
    private String fixWSResponseWithPrompt(String response) {
        return "\b\b\b\b" + response + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "\n>>> " + SET_TEXT_COLOR_GREEN;
    }

}
