package dataAccess.Exceptions;

public class UserNotFoundException extends DataAccessException{
    public UserNotFoundException() {
        super("Error: user not found");
    }
}
