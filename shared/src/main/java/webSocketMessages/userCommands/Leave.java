package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    public Leave(String authToken, int gameID) {
        super(authToken, gameID);
        commandType = CommandType.LEAVE;
    }
}
