package server.websocket;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

public class WebSocketSessions {
    private final Map<Integer, Map<String, Session>> sessionMap = new HashMap<>();

    public void addSessionToGame(int gameID, String authToken, Session session) {
        Map<String, Session> map = sessionMap.computeIfAbsent(gameID, k -> new HashMap<>());
        map.put(authToken, session);
    }
    public void removeSessionFromGame(int gameID, String authToken, Session session) {
        sessionMap.get(gameID).remove(authToken, session);
    }
    public Map<String, Session> getSessionsForGame(int gameID) {
        return sessionMap.get(gameID);
    }
}
