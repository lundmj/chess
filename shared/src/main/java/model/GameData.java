package model;
import chess.ChessGame;
public class GameData {
    private final int id;
    private final String whiteUsername;
    private final String blackUsername;
    private final String gameName;
    private final ChessGame game;

    public GameData(int id, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.id = id;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }
}
