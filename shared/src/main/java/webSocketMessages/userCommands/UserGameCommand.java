package webSocketMessages.userCommands;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public UserGameCommand(String authToken, Integer gameID) {
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }
    protected CommandType commandType;
    private final String authToken;
    private final Integer gameID;
    public String getAuthToken() {
        return authToken;
    }
    public CommandType getCommandType() {
        return commandType;
    }
    public int getGameID() {
        return gameID;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand that))
            return false;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthToken(), that.getAuthToken());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken());
    }
}