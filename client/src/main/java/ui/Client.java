package ui;

import java.util.Arrays;
import static ui.EscapeSequences.*;

public class Client {
    private State state = State.SIGNEDIN;
    private final String url;
    public Client(String url) {
        this.url = url;
    }
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String command = (tokens.length > 0)? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (command) {
            case "quit" -> "quit";
            default -> help();
        };
    }


    // vv COMMAND FUNCTIONS vv //

    private String help() {
        if (state == State.SIGNEDOUT)
            return blue("register <USERNAME> <PASSWORD> <EMAIL>") + white(" - create account\n")
                 + blue("login <USERNAME> <PASSWORD>") + white(" - to play\n")
                 + blue("quit\n")
                 + blue("help") + white(" - see this menu");
        else
            return blue("create <GAME NAME>\n")
                 + blue("list") + white(" - show available games\n")
                 + blue("join <ID> [WHITE|BLACK]") + white(" - join game as a player\n")
                 + blue("observe <ID>") + white(" - join game as an observer\n")
                 + blue("logout\n")
                 + blue("quit\n")
                 + blue("help") + white(" - see this menu");
    }
    private String blue(String string) {
        return SET_TEXT_COLOR_BLUE + string;
    }
    private String white(String string) {
        return SET_TEXT_COLOR_WHITE + string;
    }
    private String green(String string) {
        return SET_TEXT_COLOR_GREEN + string;
    }
}
