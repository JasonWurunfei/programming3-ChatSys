package programming3.chatsys.http;

import com.sun.net.httpserver.HttpExchange;
import programming3.chatsys.data.Database;
import java.io.*;

/**
 * Represents an handler for handling "recent message" request
 * @author Wu Runfei (Jason SE181)
 */
public class RecentMessagesHandler extends AbstractHandler {

    RecentMessagesHandler(Database database) {
        super(database);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("[REQUEST]: Received " + httpExchange.getRequestMethod()
                + " message: " + httpExchange.getRequestURI() + " from " + httpExchange.getRemoteAddress());
        Writer writer = new StringWriter();
        if (httpExchange.getRequestMethod().equals("GET")) {
            try {
                int n = getN(httpExchange);
                if (n > 0) {
                    writeChatMessages(writer, database.readMessages(n));
                    sendResponse(httpExchange, 200, writer.toString());
                } else {
                    sendError(httpExchange, 400, "Request invalid number of messages");
                }
            } catch (NumberFormatException error) {
                sendError(httpExchange, 400, "Number of messages in the URI " +
                        "is missing or it cannot be parsed as a number");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sendError(httpExchange, 405, "Method not allowed");
        }
    }

    /**
     * Get the "n" in the URL
     * example: http://localhost/recent/10 -> n = 10
     */
    private int getN(HttpExchange exchange) {
        String root = exchange.getHttpContext().getPath();
        String path = exchange.getRequestURI().getPath();
        return Integer.parseInt(path.substring(root.length()));
    }
}
