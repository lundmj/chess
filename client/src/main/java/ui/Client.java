package ui;

import model.AuthData;
import requests.CreateGameRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.GameInfo;
import serverFacade.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import static ui.EscapeSequences.*;

public class Client {
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
    private String authToken = null;
    private ArrayList<GameInfo> gamesList;

    public Client(String url) {
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
            server.joinGame(new JoinRequest(params[1].toUpperCase(), getRealID(params[0])), authToken);
            print_board_black_perspective();
            print_board_white_perspective();
            return "Successfully joined team: " + params[1].toUpperCase();
        }
        throw new ResponseException(400, "Expected: " + prompts.join);
    }
    private String observe(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            server.joinGame(new JoinRequest(null, getRealID(params[0])), authToken);
            print_board_black_perspective();
            print_board_white_perspective();
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
                            + blue(prompts.help) + white(" - see this menu"));
        } else {
            System.out.println(
                    blue(prompts.create + "\n")
                            + blue(prompts.list) + white(" - show available games\n")
                            + blue(prompts.join) + white(" - join game as a player\n")
                            + blue(prompts.observe) + white(" - join game as an observer\n")
                            + blue(prompts.logout + "\n")
                            + blue(prompts.quit + "\n")
                            + blue(prompts.help) + white(" - see this menu"));
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
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state == State.SIGNEDIN) {
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


    private static void print_board_white_perspective() {
        String[][] board = {
                {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
                {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
                {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
        };

        printBoard(board, false);
    }

    private static void print_board_black_perspective() {
        String[][] board = {
                {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK},
                {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
                {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}
        };

        printBoard(board, true);
    }

    private static void printBoard(String[][] board, boolean isBlackPerspective) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print((j % 2 == i % 2) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR + "1m");
                System.out.print(SET_TEXT_COLOR_WHITE); // Set the text color to white
                System.out.print(board[i][j]);
                System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
            }
            System.out.println();
        }
        if (isBlackPerspective) {
            System.out.println(SET_BG_COLOR_BLACK + "        " + RESET_BG_COLOR); // Small gap between the boards
        }
    }

}
