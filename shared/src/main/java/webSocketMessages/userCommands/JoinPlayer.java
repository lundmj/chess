package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {
    private final ChessGame.TeamColor color;
    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken, gameID);
        this.color = color;
        commandType = CommandType.JOIN_PLAYER;
    }
    public ChessGame.TeamColor getColor() {
        return color;
    }

}
