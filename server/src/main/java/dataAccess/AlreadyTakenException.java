package dataAccess;

public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException() {
        super("Error: already taken");
    }
}
