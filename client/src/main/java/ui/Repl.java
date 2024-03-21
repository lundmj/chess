package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;
    public Repl(String url) {
        this.client = new Client(url);
    }
    @SuppressWarnings("BusyWait")
    public void run() {
        System.out.println(WHITE_KNIGHT + "Welcome to Chess240!\nType help for more information.");
        Scanner scanner = new Scanner(System.in);
        String result;
        while (true) {
            promptUserInput();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                if (result.equals("quit")) {
                    System.out.print(SET_TEXT_COLOR_YELLOW + "  quitting");
                    Thread.sleep(300);
                    System.out.print(".");
                    Thread.sleep(300);
                    System.out.print(".");
                    Thread.sleep(300);
                    System.out.print(".");
                    Thread.sleep(300);
                    break;
                }
                System.out.println(SET_TEXT_COLOR_BLUE + "  " + result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.println(SET_TEXT_COLOR_RED + "  " + msg);
            }
        }
        System.out.println();
    }


    private void promptUserInput() {
        System.out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
