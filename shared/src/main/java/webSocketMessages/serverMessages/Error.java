package webSocketMessages.serverMessages;

public class Error extends ServerMessage {
    private final String message;
    public Error(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
