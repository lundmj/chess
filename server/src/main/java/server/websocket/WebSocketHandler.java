package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.Exceptions.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;
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
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(session, new Gson().fromJson(message, JoinPlayer.class));
            case JOIN_OBSERVER -> joinObserver(session, new Gson().fromJson(message, JoinObserver.class));
            case MAKE_MOVE -> makeMove(session, new Gson().fromJson(message, MakeMove.class));
            default -> throw new IOException("Invalid metadata on User Game Command");
        }
    }

    private void joinPlayer(Session session, JoinPlayer command) throws DataAccessException, IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        GameData gameData = getGame(gameID, authToken);
        boolean isWhite = (command.getColor() == ChessGame.TeamColor.WHITE);
        sessions.addSessionToGame(gameID, authToken, session);
        sendMessage(gameID, new LoadGame(new Gson().toJson(gameData)), session);
        gameData = getGame(gameID, authToken);
        String username = isWhite? gameData.whiteUsername() : gameData.blackUsername();
        String color = isWhite? "white" : "black";
        String notifMessage = String.format("%s joined %s team", username, color);
        broadcast(gameID, new Notification(notifMessage), authToken);
    }
    private void joinObserver(Session session, JoinObserver command) throws DataAccessException, IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        sessions.addSessionToGame(gameID, authToken, session);
        GameData gameData = getGame(gameID, authToken);

        sendMessage(gameID, new LoadGame(new Gson().toJson(gameData)), session);
        broadcast(gameID, new Notification("A player is observing the game"), authToken);
    }
    private void makeMove(Session session, MakeMove command) throws DataAccessException, IOException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        ChessMove move = command.getMove();
        GameData gameData = getGame(gameID, authToken);

        GameData updatedGameData;
        try {
            updatedGameData = GameService.makeMove(authToken, gameID, move, gameData, gameDAO);
        } catch (InvalidMoveException e) {
            sendMessage(gameID, new Error("Error: invalid move"), session);
            return;
        }
        broadcast(gameID, new LoadGame(new Gson().toJson(updatedGameData)), null);
        broadcast(gameID, new Notification("A player made a move"), authToken);
    }

    private <T extends ServerMessage> void sendMessage(int gameID, T message, Session session) throws IOException {
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