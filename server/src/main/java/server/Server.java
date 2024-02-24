package server;

import com.google.gson.Gson;
import requests.RegisterRequest;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", this::register);


        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object register(Request req, Response res) {
        var request = new Gson().fromJson(req.body(), RegisterRequest.class);
        String username = request.username();
        String password = request.password();
        String email = request.email();
        return new
    }

    public int port() {
        return Spark.port();
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
