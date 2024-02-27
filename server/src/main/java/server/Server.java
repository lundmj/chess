package server;

import com.google.gson.Gson;
import dataAccess.*;
import handlers.*;
import model.UserData;
import requests.RegisterRequest;
import responses.ErrorResponse;
import service.UserService;
import spark.*;

import javax.naming.BinaryRefAddr;

public class Server {
    private final UserDAO userDAO = new UserDAOMemory();
    private final AuthDAO authDAO = new AuthDAOMemory();
    private final GameDAO gameDAO = new GameDAOMemory();
    private final RegisterHandler registerHandler = new RegisterHandler(userDAO, authDAO);
    private final DeleteHandler deleteHandler = new DeleteHandler(userDAO, authDAO, gameDAO);
    private final LoginHandler loginHandler = new LoginHandler(userDAO, authDAO);
    private final LogoutHandler logoutHandler = new LogoutHandler(authDAO);
    private final CreateGameHandler createGameHandler = new CreateGameHandler(authDAO, gameDAO);
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", deleteHandler::handleRequest);
        Spark.post("/user", registerHandler::handleRequest);
        Spark.post("/session", loginHandler::handleRequest);
        Spark.delete("/session", logoutHandler::handleRequest);
        Spark.post("/game", createGameHandler::handleRequest);

        // Universal exceptions
        Spark.exception(DataAccessException.class, (e, req, res) -> {
            res.status(500);
            res.body(new Gson().toJson(new ErrorResponse(e.getMessage())));
        });
        Spark.exception(BadRequestException.class, (e, req, res) -> {
            res.status(400);
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
