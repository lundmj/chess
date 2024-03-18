import ui.Repl;

public class MainClient {
    public static void main(String[] args) {
        var url = "http://localhost:8080";
        if (args.length == 1) {
            url = args[0];
        }
        new Repl(url).run();


    }
}