package programming3.chatsys.http;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import programming3.chatsys.data.Database;
import programming3.chatsys.data.User;
import java.io.IOException;

/**
 * Represents an handler for handling "register" request
 * @author Wu Runfei (Jason SE181)
 */
public class RegisterUserHandler extends AbstractHandler {

    public RegisterUserHandler(Database database) {
        super(database);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("[REQUEST]: Received " + httpExchange.getRequestMethod()
                + " message: " + httpExchange.getRequestURI() + " from " + httpExchange.getRemoteAddress());
        if (httpExchange.getRequestMethod().equals("POST")) {
            try {
                JSONObject obj = readJSON(httpExchange.getRequestBody());
                String username = obj.optString("username");
                String fullname = obj.optString("fullname");
                String password = obj.optString("password");
                if (username.equals(""))
                    {sendError(httpExchange, 400, "username field not provided"); return;}
                if (fullname.equals(""))
                    {sendError(httpExchange, 400, "fullname field not provided"); return;}
                if (password.equals(""))
                    {sendError(httpExchange, 400, "password field not provided"); return;}

                boolean is_success = database.register(new User(username, fullname, password));
                if (is_success) {
                    sendResponse(httpExchange, 201, "Register success");
                } else {
                    sendError(httpExchange, 400, "This user name is taken");
                }
            } catch (IllegalArgumentException e) {
                switch (e.getMessage()) {
                    case "userName is invalid":
                        sendError(httpExchange, 400, "Illegal username," +
                                " username can only use letters, numbers, and underscores");
                        break;
                    case "fullName contains a line feed":
                        sendError(httpExchange, 400, "Illegal full name," +
                                " fullName cannot contains a line feed");
                        break;
                    case "password contains a line feed":
                        sendError(httpExchange, 400, "Illegal password," +
                                " password cannot contains a line feed");
                        break;
                }
            } catch (JSONException e) {
                sendError(httpExchange, 400, "JSON syntax error: " + e.getMessage());
            } catch (NullPointerException error) {
                sendError(httpExchange, 400, "Missing user information");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sendError(httpExchange, 405, "Method not allowed");
        }
    }
}
