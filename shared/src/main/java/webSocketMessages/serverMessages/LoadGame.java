package webSocketMessages.serverMessages;

public class LoadGame extends ServerMessage {
    private final String game;
    public LoadGame(String game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public String getGameJson() {
        return game;
    }
}
