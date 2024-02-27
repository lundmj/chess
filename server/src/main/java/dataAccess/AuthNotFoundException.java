package dataAccess;

public class AuthNotFoundException extends DataAccessException {
    public AuthNotFoundException() {
        super("Error: authorization not found");
    }
}
