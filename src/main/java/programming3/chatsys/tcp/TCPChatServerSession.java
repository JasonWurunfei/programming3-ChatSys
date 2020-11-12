package programming3.chatsys.tcp;

import org.json.JSONException;
import org.json.JSONObject;
import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;
import programming3.chatsys.data.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Represents a server session
 * @author Wu Runfei (Jason SE181)
 */
public class TCPChatServerSession implements Runnable {

    /*
     * This class is similar to SimpleChatServerSession class in programming3.topic4.example5
     * (e.g. connect, send, disconnect , initInputOutput methods)
     */

    Socket socket;
    Database database;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean error = false;
    private User authenticatedUser = null;
    private JSONProtocol protocol;

    TCPChatServerSession(Database database, Socket socket) {
        this.database = database;
        this.socket = socket;
    }

    /**
     * Runs the session.
     */
    @Override
    public void run() {
        System.out.println("New session started at " + this.socket);
        try {
            initInputOutput();
            for (String message = this.reader.readLine();
                 message != null; message = this.reader.readLine()) {
                System.out.println("Receive > " + message);
                handleMessage(message);
                if (error) break;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Socket timeout.");
        } catch (SocketException e) {
            System.out.println("Socket closed by client promptly.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("session " + this.socket + " ended.");
    }

    /**
     * Creates writer and reader for the interaction between server and clients.
     * @throws IOException if an I/O error occurs when creating the socket output stream
     * or if the socket is not connected
     */
    private void initInputOutput() throws IOException {
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        protocol = new JSONProtocol(writer);
    }


    /**
     * Release all the resources and close the socket.
     * @throws IOException if an I/O error occurs when closing the resources.
     */
    private void disconnect() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }

    /**
     * handle the incoming message from client according. And delegate the message
     * data and work to different protocol handle methods.
     * @param message one line message from the client
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void handleMessage(String message) throws IOException {
        JSONObject jsonMessage = null;
        try {
            jsonMessage = new JSONObject(message);
        } catch (JSONException e) {
            sendError("request must be in JSON format.");
            return;
        }
        String type = jsonMessage.optString("type");
        if (type != null) {
            switch (type) {
                case "ok": break;
                case "getunread":   getUnread();break;
                case "post":        postMessage(jsonMessage.optString("message"));break;
                case "getrecent":   getRecent(jsonMessage.optInt("n"));break;

                case "login":       login(jsonMessage.optString("username"),
                                          jsonMessage.optString("password"));break;

                case "register":    register(jsonMessage.optString("username"),
                                             jsonMessage.optString("fullname"),
                                             jsonMessage.optString("password"));break;
            }
        } else {
            sendError("Unknown request type.");
        }
    }

    /**
     * Performs the login operation defined in the protocol.
     * @param username the name of the user
     * @param password the password of the user
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void login(String username, String password) throws IOException {
        if (username == null) {sendError("Username not provided.");return;}
        if (password == null) {sendError("password not provided.");return;}

        System.out.println("login user: " + username);
        authenticatedUser = database.getUserIfAuthenticated(username, password);
        if(authenticatedUser != null) {
            this.sendOk();
        } else {
            this.sendError("Wrong username or password.");
        }
    }

    /**
     * Performs the register operation defined in the protocol.
     * @param username the name of the user
     * @param fullName the full name of the user
     * @param password the password of the user
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void register(String username, String fullName, String password) throws IOException {
        if (username == null) {this.sendError("Username not provided.");return;}
        if (fullName == null) {this.sendError("full name not provided.");return;}
        if (password == null) {this.sendError("password not provided.");return;}

        System.out.println("registering user ...");
        System.out.println("fullName > " + username);
        System.out.println("fullName > " + fullName);
        System.out.println("fullName > " + password);

        try {
            if(database.register(new User(username, fullName, password))) {
                System.out.println("register user "+ username + " success.");
                this.sendOk();
            } else {
                this.sendError("register user "+ username + " failed. " +
                          "This username is taken by other user.");
            }
        } catch (IllegalArgumentException e) {
            if ("userName is invalid".equals(e.getMessage())) {
                this.sendError("userName is invalid.");
            } else {
                this.sendError("error occurred when registering.");
            }
        }
    }

    /**
     * Performs the login operation defined in the protocol.
     * @param message the message content
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void postMessage(String message) throws IOException {
        if (message == null) {this.sendError("Post messages not provided.");return;}

        if (authenticatedUser != null) {
            database.addMessage(authenticatedUser.getUserName(), message);
            System.out.println("user: " + authenticatedUser.getUserName() +
                    "\npost message: " + message);
            this.sendOk();
        } else {
            this.sendError("User is not authenticated.");
        }
    }

    /**
     * Performs the get unread message operation defined in the protocol.
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void getUnread() throws IOException {
        System.out.println("getUnread");
        if (authenticatedUser != null) {
            this.sendRespond(formatMessages(database.getUnreadMessages(
                    authenticatedUser.getUserName()
            )));
        } else {
            this.sendError("User is not authenticated.");
        }
    }

    /**
     * Performs the get recent message operation defined in the protocol.
     * @param num number of the most recent messages will be returned.
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void getRecent(int num) throws IOException {
        if (num > 0)
            this.sendRespond(formatMessages(database.readMessages(num)));
        else {
            this.sendError("request 0 or invalid number of messages");
        }
    }

    /**
     * Formats a list of messages into a server responds specified in the protocol.
     * @param messages list of ChatMessage objects that is about to be send.
     */
    private String formatMessages(List<ChatMessage> messages) {
        String respond = "";

        JSONObject numberOfMessages = new JSONObject();
        numberOfMessages.put("type", "messages");
        numberOfMessages.put("n", messages.size());
        respond += numberOfMessages.toString() + "\r\n";

        JSONObject message = new JSONObject();
        for (ChatMessage chatMessage : messages) {
            message.put("type", "message");
            message.put("message", protocol.formatChatMessage(chatMessage));
            respond += message.toString() + "\r\n";
        }

        return respond.substring(0, respond.length()-2);
    }

    /**
     * Sends an error message to client.
     * @param error error message
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void sendError(String error) throws IOException {
        JSONObject jsonError = new JSONObject();
        jsonError.put("type", "error");
        jsonError.put("message", error);
        System.out.println("[ERROR]: " + jsonError.toString());
        this.sendRespond(jsonError.toString());
        this.error = true;
    }

    /**
     * Sends a respond message to the client.
     * @param respond respond message
     * @throws IOException if an I/O error occurs when sending the respond message.
     */
    private void sendRespond(String respond) throws IOException {
        writer.write(respond + "\r\n");
        writer.flush();
    }

    private void sendOk() throws IOException {
        this.sendRespond("{\"type\":\"ok\"}");
    }

}
