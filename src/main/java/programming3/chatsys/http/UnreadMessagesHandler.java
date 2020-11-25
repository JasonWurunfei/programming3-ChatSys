package programming3.chatsys.http;

import com.sun.net.httpserver.HttpExchange;
import programming3.chatsys.data.Database;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Represents an handler for handling "unread message" request
 * @author Wu Runfei (Jason SE181)
 */
public class UnreadMessagesHandler extends AbstractHandler {

    public UnreadMessagesHandler(Database database) {
        super(database);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("[REQUEST]: Received " + httpExchange.getRequestMethod()
                + " message: " + httpExchange.getRequestURI() + " from " + httpExchange.getRemoteAddress());
        Writer writer = new StringWriter();
        if (httpExchange.getRequestMethod().equals("GET")) {
            try {
                if (authenticate(httpExchange.getRequestURI())) {
                    writeChatMessages(writer, database.getUnreadMessages(
                            parseQuery(httpExchange).get("username")));
                    sendResponse(httpExchange, 200, writer.toString());
                } else {
                    sendError(httpExchange, 401, "Invalid username or password");
                }
            } catch (NullPointerException error) {
                sendError(httpExchange, 400, "Authentication information not provided");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sendError(httpExchange, 405, "Method not allowed");
        }
    }
}
