package ui;

import chess.*;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
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
import java.util.Scanner;

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
        String redraw = "redraw";
        String leave = "leave";
        String move = "move [a-h] [1-8] [a-h] [1-8]";
        String resign = "resign";
        String highlight = "highlight [a-h] [1-8]";
    }

    private final Prompts prompts = new Prompts();
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private final String url;
    private String authToken = null;
    private ArrayList<GameInfo> gamesList;
    private ChessGame.TeamColor color = null;
    private GameData gameData = null;

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
            case "leave" -> leave();
            case "redraw" -> redraw();
            case "move" -> move(params);
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
            this.authToken = auth.authToken();
            state = State.SIGNEDIN;
            return "Successfully registered: " + auth.username();
        } else throw new ResponseException(400, "Expected: " + prompts.register);
    }

    private String login(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 2) {
            AuthData auth = server.login(new LoginRequest(params[0], params[1]));
            this.authToken = auth.authToken();
            state = State.SIGNEDIN;
            return "Successfully logged in: " + auth.username();
        } else throw new ResponseException(400, "Expected: " + prompts.login);
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        assertNotInGame();
        server.logout(authToken);
        this.authToken = null;
        state = State.SIGNEDOUT;
        return "Logged out";
    }

    private String createGame(String... params) throws ResponseException {
        assertSignedIn();
        assertNotInGame();
        if (params.length != 0) {
            String name = String.join("_", params);
            server.createGame(new CreateGameRequest(name), authToken);
            return "Successfully created game: " + name;
        } else throw new ResponseException(400, "Expected: " + prompts.create);
    }

    private String list() throws ResponseException {
        assertSignedIn();
        assertNotInGame();
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
        assertNotInGame();
        if (params.length == 2) {
            String color = params[1].toUpperCase();
            int gameID = getRealID(params[0]);
            server.joinGame(new JoinRequest(color, gameID), authToken);
            this.color = (color.equals("WHITE")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            new WebSocketFacade(url, this).joinPlayer(authToken, gameID, this.color);
            state = State.PLAYING;
            return "Successfully joined team: " + color;
        } else throw new ResponseException(400, "Expected: " + prompts.join);
    }
    private String observe(String... params) throws ResponseException {
        assertSignedIn();
        assertNotInGame();
        if (params.length == 1) {
            int gameID = getRealID(params[0]);
            server.joinGame(new JoinRequest(null, gameID), authToken);
            new WebSocketFacade(url, this).joinObserver(authToken, gameID);
            state = State.OBSERVING;
            return "Successfully observing game";
        } else throw new ResponseException(400, "Expected: " + prompts.observe);
    }

    private String redraw() throws ResponseException {
        assertInGame();
        showBoard();
        return "Refreshed";
    }
    private String leave() throws ResponseException {
        assertInGame();
        state = State.SIGNEDIN;
        new WebSocketFacade(url, this).leave(authToken, gameData.id());
        return "Successfully left game";
    }
    private String resign() throws ResponseException {
        assertPlaying();
        new WebSocketFacade(url, this).resign(authToken, gameData.id());
        return "Resigned";
    }
    private String move(String... params) throws ResponseException {
        assertPlaying();
        if (params.length >= 4) {
            int startX = getIntFromLetter(params[0]);
            int startY = Integer.parseInt(params[1]);
            int endX = getIntFromLetter(params[2]);
            int endY = Integer.parseInt(params[3]);
            ChessPosition start = new ChessPosition(startX, startY);
            ChessPosition end = new ChessPosition(endX, endY);
            ChessGame game = gameData.game();
            ChessBoard board = game.getBoard();
            ChessPiece piece = board.getPiece(start);
            ChessPiece.PieceType promotionPiece = null;
            if (piece.equals(new ChessPiece(color, ChessPiece.PieceType.PAWN))
            && (piece.getTeamColor() == ChessGame.TeamColor.WHITE && endY == 8) || piece.getTeamColor() == ChessGame.TeamColor.BLACK && endY == 1) {
                while (promotionPiece == null) {
                    System.out.print("What piece would you like to promote your pawn to?\n" +
                            "(Enter QUEEN, KNIGHT, ROOK, or BISHOP");
                    Scanner scanner = new Scanner(System.in);
                    promotionPiece = switch (scanner.nextLine()) {
                        case "QUEEN" -> ChessPiece.PieceType.QUEEN;
                        case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
                        case "ROOK" -> ChessPiece.PieceType.ROOK;
                        case "BISHOP" -> ChessPiece.PieceType.BISHOP;
                        default -> null;
                    };
                }
            }
            ChessMove move = new ChessMove(start, end, promotionPiece);
            new WebSocketFacade(url, this).makeMove(authToken, gameData.id(), move);
            return "Move request sent...";
        } else throw new ResponseException(400, "Expected: " + prompts.move);
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
        } else if (state == State.SIGNEDIN) {
            System.out.println(
                blue(prompts.create + "\n")
              + blue(prompts.list) + white(" - show available games\n")
              + blue(prompts.join) + white(" - join game as a player\n")
              + blue(prompts.observe) + white(" - join game as an observer\n")
              + blue(prompts.logout + "\n")
              + blue(prompts.quit + "\n")
              + blue(prompts.help) + white(" - see this menu")
            );
        } else if (state == State.PLAYING) {
            System.out.println(
                blue(prompts.redraw) + white(" - redraw the chess board\n")
              + blue(prompts.leave) + white(" - leave the current game\n")
              + blue(prompts.move) + white(" - select a piece to move\n")
              + blue(prompts.resign) + white(" - elect to resign and lose this game\n")
              + blue(prompts.highlight) + white(" - see possible moves for selected piece\n")
              + blue(prompts.help) + white(" - see this menu")
            );
        } else /* OBSERVING */ {
            System.out.println(
                blue(prompts.redraw) + white(" - redraw the chess board\n")
              + blue(prompts.leave) + white(" - leave the current game\n")
              + blue(prompts.highlight) + white(" - see possible moves for selected piece\n")
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
        if (state != State.SIGNEDIN && state != State.OBSERVING
                && state != State.PLAYING) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state != State.SIGNEDOUT) {
            throw new ResponseException(400, "You must be signed out");
        }
    }
    private void assertInGame() throws ResponseException {
        if (state != State.OBSERVING && state != State.PLAYING) {
            throw new ResponseException(400, "You must join or observe a game");
        }
    }
    private void assertNotInGame() throws ResponseException {
        if (state == State.PLAYING || state == State.OBSERVING) {
            throw new ResponseException(400, "You must leave the game");
        }
    }
    private void assertPlaying() throws ResponseException {
        if (state != State.PLAYING) {
            throw new ResponseException(400, "You must join a game");
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
    private int getIntFromLetter(String letter) throws ResponseException {
        return switch (letter) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new ResponseException(400, "Expected a letter a-h, lowercase");
        };
    }

    //Display Server Response Methods//
    public void notify(Notification message) {
        System.out.print(fixWSResponseWithPrompt(SET_TEXT_COLOR_MAGENTA + message.getMessage()));
    }
    public void loadGame(LoadGame message) {
        String gameJson = message.getGameJson();
        gameData = new Gson().fromJson(gameJson, GameData.class);
        showBoard();
    }
    public void displayError(Error message) {

    }
    private String fixWSResponseWithPrompt(String response) {
        return "\b\b\b\b" + response + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "\n>>> " + SET_TEXT_COLOR_GREEN;
    }
    private void showBoard() {
        System.out.print(fixWSResponseWithPrompt((color == ChessGame.TeamColor.BLACK) ?
                gameData.game().getBoard().getBlackPerspective() : gameData.game().getBoard().getWhitePerspective()));
    }

}
