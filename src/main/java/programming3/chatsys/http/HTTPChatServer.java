package programming3.chatsys.http;

import com.sun.net.httpserver.HttpServer;
import programming3.chatsys.data.Database;
import programming3.chatsys.data.SQLiteDatabase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

/**
 * Represents an HTTP chat server
 * @author Wu Runfei (Jason SE181)
 */
public class HTTPChatServer {
    private Database database;
    private HttpServer server;

    public HTTPChatServer(Database database,  int port) throws IOException {
        this.database = database;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.initContexts();
    }

    /**
     * Initialize contexts
     */
    private void initContexts() {
        this.server.createContext("/recent/", new RecentMessagesHandler(database));
        this.server.createContext("/unread/", new UnreadMessagesHandler(database));
        this.server.createContext("/message/", new PostMessageHandler(database));
        this.server.createContext("/user/", new RegisterUserHandler(database));
    }

    /**
     * Starts the server
     */
    public void start() {
        this.server.start();
        System.out.println("[INFO]: Server started at " +
                this.server.getAddress() + " and ready for connection ...");
    }

    /**
     * Stops the server
     */
    public void stop() {
        this.server.stop(0);
        System.out.println("[INFO]: Server stopped.");
    }

    public static void main(String[] args) throws SQLException, IOException {
        new HTTPChatServer(new SQLiteDatabase("test.sqlite"), 8080).start();
    }
}
