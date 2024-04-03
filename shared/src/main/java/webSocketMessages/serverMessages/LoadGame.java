package webSocketMessages.serverMessages;

public class LoadGame extends ServerMessage {
    private final String gameJson;
    public LoadGame(String gameJson) {
        super(ServerMessageType.LOAD_GAME);
        this.gameJson = gameJson;
    }

    public String getGameJson() {
        return gameJson;
    }
}
