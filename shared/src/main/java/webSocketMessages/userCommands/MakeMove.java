package webSocketMessages.userCommands;

public class MakeMove extends UserGameCommand {
    public MakeMove(String authToken, int gameID) {
        super(authToken, gameID);
        commandType = CommandType.MAKE_MOVE;
    }
}
