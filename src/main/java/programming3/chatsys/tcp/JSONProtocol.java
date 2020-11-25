package programming3.chatsys.tcp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.User;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

/**
 * A class that is used to format and parse JSON objects
 * @author Wu Runfei (Jason SE181)
 */
public class JSONProtocol {
    private BufferedReader reader;
    private Writer writer;
    private JSONTokener tokener;

    /**
     * Creates a JSONProtocol object
     * @param writer writer used to write in data
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    public JSONProtocol(Writer writer) {
        this.writer = writer;
    }

    /**
     * Reused from "programming3.topic5.example4.JSONProtocol" provided in Moodle
     * Creates a JSONProtocol object
     * @param input input stream used to read in data
     * @param output output stream used to write in data
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    public JSONProtocol(InputStream input, OutputStream output) {
        this.init(input, output);
    }

    /**
     * Reused from "programming3.topic5.example4.JSONProtocol" provided in Moodle
     * Creates a JSONProtocol object from a socket object.
     * @param socket used for I/O operations through the Internet
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    public JSONProtocol(Socket socket) throws IOException {
        this.init(socket.getInputStream(), socket.getOutputStream());
    }

    /**
     * Reused from "programming3.topic5.example4.JSONProtocol" provided in Moodle
     * Initializes a JSONProtocol object
     * @param input input stream used to read in data
     * @param output output stream used to write in data
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    private void init(InputStream input, OutputStream output) {
        this.reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8)
        );
        this.writer = new BufferedWriter(
                new OutputStreamWriter(output, StandardCharsets.UTF_8)
        );
        this.tokener = new JSONTokener(this.reader);
    }

    /**
     * Creates a JSONObject describing a User object based on the given params
     * @param userName the name of this user
     * @param fullName the full name of this user
     * @param password the password of this user
     * @return obj formatted User object
     */
    public JSONObject formatUser(String userName, String fullName, String password) {
        JSONObject obj = new JSONObject();
        obj.put("username", userName);
        obj.put("fullname", fullName);
        obj.put("password", password);
        return obj;
    }

    /**
     * Creates a JSONObject describing a User object based on the given User object
     * @param user User object that is about to be converted into JSONObject
     * @return obj formatted User object
     */
    public JSONObject formatUser(User user) {
        return this.formatUser(user.getUserName(), user.getFullName(), user.getPassword());
    }

    /**
     * Creates a User object described by JSONObject
     * @param JSONUser JSONObject that is going to be parsed to User object
     * @return user User object produced by parsing
     * @throws JSONException if username or fullname or password is not provided
     */
    public User parseUser(JSONObject JSONUser) throws JSONException {
        return new User(
                JSONUser.getString("username"),
                JSONUser.getString("fullname"),
                JSONUser.getString("password")
        );
    }

    /**
     * Writes a User object in the Writer as JSON
     * @param user User object that is going to be write
     * @throws IOException if an I/O error occurs
     */
    public void writeUser(User user) throws IOException {
        JSONObject obj = formatUser(user);
        this.writer.write(obj.toString());
    }

    /**
     * Creates a JSONObject describing a ChatMessage object based on the given params
     * @param id the id of this chat message
     * @param message the message centent of this chat message
     * @param username the username of this chat message
     * @param timestamp the timestamp of this chat message
     * @return obj formatted ChatMessage object
     */
    public JSONObject formatChatMessage(int id, String message, String username, long timestamp) {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("message", message);
        obj.put("username", username);
        obj.put("timestamp", timestamp);
        return obj;
    }

    /**
     * Creates a JSONObject describing a ChatMessage object based on the given ChatMessage object
     * @param message ChatMessage object that is about to be converted into JSONObject
     * @return obj formatted ChatMessage object
     */
    public JSONObject formatChatMessage(ChatMessage message) {
        return this.formatChatMessage(
                message.getId(),
                message.getMessage(),
                message.getUserName(),
                message.getTimestamp().getTime()
        );
    }

    /**
     * Creates a JSONObject describing a array of ChatMessage objects
     * @param messages ChatMessage list object that is about to be converted into JSONArray
     * @return obj formatted ChatMessage objects
     */
    public JSONArray formatChatMessages(List<ChatMessage> messages) {
        JSONArray array = new JSONArray();
        for (ChatMessage message : messages) {
            array.put(formatChatMessage(message));
        }
        return array;
    }

    /**
     * Creates a ChatMessage object described by JSONObject
     * @param JSONUser JSONObject that is going to be parsed to ChatMessage object
     * @return user User object produced by parsing
     * @throws JSONException if id or username or timestamp or message is not provided
     */
    public ChatMessage parseChatMessage(JSONObject JSONUser) throws JSONException {
        return new ChatMessage(
                JSONUser.getInt("id"),
                JSONUser.getString("username"),
                new Timestamp(JSONUser.getLong("timestamp")),
                JSONUser.getString("message")
        );
    }

    /**
     * Writes a ChatMessage object in the Writer as JSON
     * @param message ChatMessage object that is going to be write
     * @throws IOException if an I/O error occurs
     */
    public void writeChatMessage(ChatMessage message) throws IOException {
        JSONObject obj = formatChatMessage(message);
        this.writer.write(obj.toString());
    }

    public Writer getWriter() {
        return writer;
    }
}
