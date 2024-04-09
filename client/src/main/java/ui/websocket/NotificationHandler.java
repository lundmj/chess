package ui.websocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

public interface NotificationHandler {
    void notify(Notification message);
    void loadGame(LoadGame message);
    void displayError(Error message);
}

