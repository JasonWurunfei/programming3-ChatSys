package programming3.chatsys.tcp;

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
        } catch (IOException e) {
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
        Protocol.MatchTuple matchTuple = Protocol.findMatch(message);
        if (matchTuple != null) {
            switch (matchTuple.type) {
                case "OK": break;
                case "GET_UNREAD":   getUnread(); break;
                case "POST_MESSAGE": postMessage(matchTuple.matcher.group("message")); break;
                case "GET_RECENT":   getRecent(matchTuple.matcher.group("num")); break;

                case "LOGIN":        login(matchTuple.matcher.group("username"),
                                           matchTuple.matcher.group("password"));break;

                case "REGISTER":     register(matchTuple.matcher.group("username"),
                                              matchTuple.matcher.group("fullName"),
                                              matchTuple.matcher.group("password")); break;
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
        System.out.println("login user: " + username);

        if((authenticatedUser = database.getUserIfAuthenticated(username, password)) != null) {
            sendRespond("OK");
        } else {
            sendError("Wrong username or password.");
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
        System.out.println("registering user ...");
        System.out.println("fullName > " + username);
        System.out.println("fullName > " + fullName);
        System.out.println("fullName > " + password);

        try {
            if(database.register(new User(username, fullName, password))) {
                System.out.println("register user "+ username + " success.");
                sendRespond("OK");
            } else {
                System.out.println();
                sendError("register user "+ username + " failed. " +
                          "This username is taken by other user.");
            }
        } catch (IllegalArgumentException e) {
            if ("userName is invalid".equals(e.getMessage())) {
                sendError("userName is invalid.");
            } else {
                sendError("error occurred when registering.");
            }
        }
    }

    /**
     * Performs the login operation defined in the protocol.
     * @param message the message content
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void postMessage(String message) throws IOException {
        if (authenticatedUser != null) {
            database.addMessage(authenticatedUser.getUserName(), message);
            System.out.println("user: " + authenticatedUser.getUserName() +
                    "\npost message: " + message);
            sendRespond("OK");
        } else {
            sendError("User is not authenticated.");
        }
    }

    /**
     * Performs the get unread message operation defined in the protocol.
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void getUnread() throws IOException {
        System.out.println("getUnread");
        if (authenticatedUser != null) {
            sendRespond(formatMessages(database.getUnreadMessages(
                    authenticatedUser.getUserName()
            )));
        } else {
            sendError("User is not authenticated.");
        }
    }

    /**
     * Performs the get recent message operation defined in the protocol.
     * @param num number of the most recent messages will be returned.
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void getRecent(String num) throws IOException {
        try {
            int numOfMsg = Integer.parseInt(num);
            if (numOfMsg < 0)
                throw new NumberFormatException("request negative number of messages");
            sendRespond(formatMessages(database.readMessages(numOfMsg)));
        } catch (NumberFormatException e) {
            sendError("Invalid argument " + num + ".");
        }
    }

    /**
     * Formats a list of messages into a server responds specified in the protocol.
     * @param messages list of ChatMessage objects that is about to be send.
     */
    private String formatMessages(List<ChatMessage> messages) {
        String respond = "MESSAGES " + messages.size() + "\r\n";
        for (ChatMessage chatMessage : messages) {
            respond += "MESSAGE " + chatMessage.getUserName() +
                    " " + chatMessage.getTimestamp() + " "+chatMessage.getMessage() + "\r\n";
        }
        return respond.substring(0, respond.length()-2);
    }

    /**
     * Sends an error message to client.
     * @param error error message
     * @throws IOException if an I/O error occurs when sending the error message.
     */
    private void sendError(String error) throws IOException {
        String errorMessage = "ERROR " + error;
        System.out.println(errorMessage);
        sendRespond(errorMessage);
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
}
