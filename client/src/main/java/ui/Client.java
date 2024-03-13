package ui;

public class Client {
    public Client(String url) {

    }
    public String eval(String input) {
        return switch (input) {
            case "quit" -> "quit";
            default -> "default";
        };
    }
}
