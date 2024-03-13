package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;
    public Repl(String url) {
        this.client = new Client(url);
    }
    public void run() {
        System.out.println(BLACK_KNIGHT + "Welcome to Chess240!");
        System.out.print("<<help placeholder>>");
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            promptUserInput();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void promptUserInput() {
        System.out.print(SET_TEXT_COLOR_WHITE + "\n>>> " + RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_GREEN);
    }
}
