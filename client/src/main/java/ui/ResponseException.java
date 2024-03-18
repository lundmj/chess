package ui;

public class ResponseException extends Exception{
    public ResponseException(int status, String message) {
        super(message);
    }
}
