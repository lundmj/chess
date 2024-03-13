package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;
    public Repl(String url) {
        this.client = new Client(url);
    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        promptUserInput();
        String line = scanner.next();
    }
    private void promptUserInput() {
        System.out.print("\n>>> " + RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_GREEN);
    }
}
