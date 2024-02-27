package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.Exceptions.BadRequestException;
import dataAccess.Exceptions.DataAccessException;
import handlers.*;
import responses.ErrorResponse;
import spark.*;

public class Server {
    private final UserDAO userDAO = new UserDAOMemory();
    private final AuthDAO authDAO = new AuthDAOMemory();
    private final GameDAO gameDAO = new GameDAOMemory();
    private final RegisterHandler registerHandler = new RegisterHandler(userDAO, authDAO);
    private final DeleteHandler deleteHandler = new DeleteHandler(userDAO, authDAO, gameDAO);
    private final LoginHandler loginHandler = new LoginHandler(userDAO, authDAO);
    private final LogoutHandler logoutHandler = new LogoutHandler(authDAO);
    private final ListGamesHandler listGamesHandler = new ListGamesHandler(authDAO, gameDAO);
    private final CreateGameHandler createGameHandler = new CreateGameHandler(authDAO, gameDAO);
    private final JoinGameHandler joinGameHandler = new JoinGameHandler(authDAO, gameDAO);
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", deleteHandler::handleRequest);
        Spark.post("/user", registerHandler::handleRequest);
        Spark.post("/session", loginHandler::handleRequest);
        Spark.delete("/session", logoutHandler::handleRequest);
        Spark.get("/game", listGamesHandler::handleRequest);
        Spark.post("/game", createGameHandler::handleRequest);
        Spark.put("/game", joinGameHandler::handleRequest);

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
