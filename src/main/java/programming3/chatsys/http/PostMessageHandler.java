package programming3.chatsys.http;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import programming3.chatsys.data.Database;
import java.io.IOException;
import java.util.Map;

/**
 * Represents an handler for handling "post message" request
 * @author Wu Runfei (Jason SE181)
 */
public class PostMessageHandler extends AbstractHandler {

    public PostMessageHandler(Database database) {
        super(database);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("[REQUEST]: Received " + httpExchange.getRequestMethod()
                + " message: " + httpExchange.getRequestURI() + " from " + httpExchange.getRemoteAddress());
        if (httpExchange.getRequestMethod().equals("POST")) {
            try {
                if (authenticate(httpExchange.getRequestURI())) {
                    Map<String, String> query = parseQuery(httpExchange);

                    String message = readJSON(httpExchange.getRequestBody()).optString("message");
                    if (message.equals(""))
                        {sendError(httpExchange, 400, "message field not provided"); return;}

                    database.addMessage(
                            query.get("username"),
                            message
                    );
                    sendResponse(httpExchange, 201, "OK");
                } else {
                    sendError(httpExchange, 401, "Invalid username or password");
                }
            } catch (JSONException e) {
                sendError(httpExchange, 400, "JSON syntax error: " + e.getMessage());
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
