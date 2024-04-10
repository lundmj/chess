package webSocketMessages.userCommands;

public class Resign extends UserGameCommand {
    public Resign(String authToken, int gameID) {
        super(authToken, gameID);
        commandType = CommandType.RESIGN;
    }
}
