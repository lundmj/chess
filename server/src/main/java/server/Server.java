package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.*;
import handlers.*;
import responses.ErrorResponse;
import spark.*;

public class Server {
    private final UserDAO userDAO = new UserDAOMemory();
    private final AuthDAO authDAO = new AuthDAOMemory();
    private final GameDAO gameDAO = new GameDAOMemory();
    public int run(int port) {
        Spark.port(port);

        Spark.staticFiles.location("web");

        // Endpoints
        Spark.delete("/db", new DeleteHandler(userDAO, authDAO, gameDAO)::handleRequest);
        Spark.post("/user", new RegisterHandler(userDAO, authDAO)::handleRequest);
        Spark.post("/session", new LoginHandler(userDAO, authDAO)::handleRequest);
        Spark.delete("/session", new LogoutHandler(authDAO)::handleRequest);
        Spark.get("/game", new ListGamesHandler(authDAO, gameDAO)::handleRequest);
        Spark.post("/game", new CreateGameHandler(authDAO, gameDAO)::handleRequest);
        Spark.put("/game", new JoinGameHandler(authDAO, gameDAO)::handleRequest);

        // Exceptions
        Spark.exception(DataAccessException.class, (e, req, res) -> {
            res.status(500);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.exception(BadRequestException.class, (e, req, res) -> {
            res.status(400);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.exception(UnauthorizedException.class, (e, req, res) -> {
            res.status(401);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.exception(AlreadyTakenException.class, (e, req, res) -> {
            res.status(403);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
