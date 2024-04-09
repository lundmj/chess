package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.*;
import handlers.*;
import responses.ErrorResponse;
import server.websocket.WebSocketHandler;
import spark.*;

public class Server {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Server() {
        try {
            this.userDAO = new UserDAOSQL();
            this.authDAO = new AuthDAOSQL();
            this.gameDAO = new GameDAOSQL();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public int run(int port) {

        Spark.port(port);
        // Websocket
        Spark.webSocket("/connect", new WebSocketHandler(authDAO, gameDAO));

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
        Spark.exception(DataAccessException.class, (e, req, res) -> {
            res.status(500);
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
