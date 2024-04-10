package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand {
    public JoinObserver(String authToken, int gameID) {
        super(authToken, gameID);
        commandType = CommandType.JOIN_OBSERVER;
    }
}
