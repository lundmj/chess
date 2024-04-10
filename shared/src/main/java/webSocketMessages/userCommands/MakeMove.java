package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {
    private final ChessMove move;
    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken, gameID);
        commandType = CommandType.MAKE_MOVE;
        this.move = move;
    }
    public ChessMove getMove() {
        return move;
    }
}
