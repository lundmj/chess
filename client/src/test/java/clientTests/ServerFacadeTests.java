package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.Client;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static Client client;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        client = new Client(String.format("http://localhost:%s", port));
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clear() {
        assertDoesNotThrow(() -> client.eval("clearall"));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test @DisplayName("Good Register")
    public void goodRegister() {
        assertDoesNotThrow(() -> {
            String result = client.eval("register m m m");
            assertEquals(result, "Successfully registered user: m");
        });
    }
    @Test @DisplayName("Bad Register")
    public void badRegister() {
        Assertions.assertTrue(true);
    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//    @Test @DisplayName("")
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
//

}
