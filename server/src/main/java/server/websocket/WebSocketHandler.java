package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@WebSocket
public class WebSocketHandler {
    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    private final WebSocketSessions sessions = new WebSocketSessions();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        String metadata = message.substring(0,2);
        String data = message.substring(2);
        switch (metadata) {
            case "jp" -> joinPlayer(session, new Gson().fromJson(data, JoinPlayer.class));
            default -> throw new IOException("Invalid metadata on User Game Command");
        }
    }

    private void joinPlayer(Session session, JoinPlayer command) throws DataAccessException, IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthString();
        sessions.addSessionToGame(gameID, authToken, session);
        GameData game = getGame(gameID, authToken);

        sendMessage(gameID, new LoadGame(new Gson().toJson(game)), authToken);

        boolean isWhite = command.getColor() == ChessGame.TeamColor.WHITE;
        String username = isWhite? game.whiteUsername() : game.blackUsername();
        String color = isWhite? "white" : "black";
        String notifMessage = String.format("%s joined %s team", username, color);
        broadcast(gameID, new Notification(notifMessage), authToken);
    }

    private <T extends ServerMessage> void sendMessage(int gameID, T message, String authToken) throws IOException {
        Session session = sessions.getSessionsForGame(gameID).get(authToken);
        if (session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }
    private void broadcast(int gameID, ServerMessage message, String exceptThisAuthToken) throws IOException {
        Map<String, Session> sessionsMap = sessions.getSessionsForGame(gameID);
        for (String auth : sessionsMap.keySet()) {
            Session session = sessionsMap.get(auth);
            if (session.isOpen() && !auth.equals(exceptThisAuthToken)) {
                session.getRemote().sendString(new Gson().toJson(message));
            }
        }
    }

    private GameData getGame(int gameID, String authToken) throws DataAccessException {
        ArrayList<GameData> games = GameService.listGames(authToken, authDAO, gameDAO);
        GameData game = null;
        for (GameData gameData : games) {
            if (gameData.id() == gameID) {
                game = gameData;
                break;
            }
        }
        if (game == null) throw new DataAccessException("Error: game not found");

        return game;
    }
}