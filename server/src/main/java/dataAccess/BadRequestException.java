package dataAccess;

public class BadRequestException extends DataAccessException {
    public BadRequestException() {
        super("Error: bad request");
    }
}
