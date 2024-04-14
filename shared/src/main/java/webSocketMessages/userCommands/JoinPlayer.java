package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {
    private final ChessGame.TeamColor playerColor;
    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken, gameID);
        this.playerColor = playerColor;
        assert playerColor != null;
        commandType = CommandType.JOIN_PLAYER;
    }
    public ChessGame.TeamColor getColor() {
        return playerColor;
    }

}
