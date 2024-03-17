package ui;

import java.util.Arrays;

public class Client {
    public Client(String url) {

    }
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String command = (tokens.length > 0)? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (command) {
            case "quit" -> "quit";
            default -> "default";
        };
    }
}
