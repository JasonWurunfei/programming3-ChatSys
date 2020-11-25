package programming3.chatsys.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;
import programming3.chatsys.tcp.JSONProtocol;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an abstract handler for handling http requests
 * @author Wu Runfei (Jason SE181)
 */
public abstract class AbstractHandler implements HttpHandler{
    protected Database database;

    AbstractHandler(Database database) {
        this.database = database;
    }

    /**
     * Reads a JSONObject from a input stream
     * @param stream input stream
     * @return JSON object read JSONObject
     */
    protected JSONObject readJSON (InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return new JSONObject(reader.readLine());
    }

    /**
     * Writes a JSONObject into a writer
     * @param writer used to write
     * @param obj JSONObject need to be written
     */
    protected void writeJSON(Writer writer, JSONObject obj) throws IOException {
        writer.write(obj.toString());
    }

    /**
     * Check if the username and password are matching.
     * @param uri URI which contains username and password
     * @return true if username and password are matching. Otherwise, false.
     */
    protected boolean authenticate (URI uri) {
        String query = uri.getQuery();
        Map<String, String> result = parseQuery(query);

        String username = result.get("username");
        String password = result.get("password");

        if (username == null || password == null) return false;
        return database.authenticate(username, password);
    }

    /**
     * Writes a list of ChatMessage objects in JSON format
     * @param writer used to write
     * @param messages list of ChatMessage objects about to write.
     * @throws IOException if I/O error occurs
     */
    protected void writeChatMessages (Writer writer, List<ChatMessage> messages) throws IOException {
        JSONProtocol protocol = new JSONProtocol(writer);
        JSONObject response = new JSONObject();
        response.put("messages", protocol.formatChatMessages(messages));
        writeJSON(writer, response);
    }

    /**
     * Direct reused from Programming3.topic7.example3.MessageHandler
     * Parse URL query into Map
     * @param exchange HttpExchange object
     * @return map that contains key-value pairs specified in URL query
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    protected static Map<String, String> parseQuery(HttpExchange exchange) {
        return parseQuery(exchange.getRequestURI().getQuery());
    }

    /**
     * Reused from Programming3.topic7.example3.MessageHandler
     * Parse URL query into Map
     * @param query URL query string
     * @return map that contains key-value pairs specified in URL query
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    protected static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=", 2);
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], null);
            }
        }
        return result;
    }

    /**
     * Reused from Programming3.topic7.example3.MessageHandler
     * Send response to client.
     * @param exchange HttpExchange object
     * @param code response code
     * @param response response message
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    protected void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        System.out.println("[REPLY]: " + code + " to " + exchange.getRemoteAddress());
        exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
        if (!exchange.getRequestMethod().equals("HEAD")) {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(exchange.getResponseBody(), StandardCharsets.UTF_8));
            writer.write(response);
            writer.flush();
            writer.close();
        }
    }

    /**
     * Send error response to client
     * @param exchange HttpExchange object
     * @param code response code
     * @param errorMessage error message
     * @throws IOException if I/O error occurs
     */
    protected void sendError(HttpExchange exchange, int code, String errorMessage) throws IOException {
        System.out.println("[ERROR]: " + errorMessage);
        JSONObject error = new JSONObject();
        error.put("error", errorMessage);
        sendResponse(exchange, code, error.toString());
    }

    /**
     * Handles http request of different contexts
     * @param httpExchange HttpExchange object
     * @throws IOException if I/O error occurs
     */
    abstract public void handle(HttpExchange httpExchange) throws IOException;
}
