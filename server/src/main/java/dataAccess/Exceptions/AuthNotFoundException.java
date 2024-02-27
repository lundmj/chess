package dataAccess.Exceptions;

public class AuthNotFoundException extends DataAccessException {
    public AuthNotFoundException() {
        super("Error: authorization not found");
    }
}
