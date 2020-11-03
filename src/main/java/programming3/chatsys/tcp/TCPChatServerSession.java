package programming3.chatsys.tcp;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;
import programming3.chatsys.data.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPChatServerSession implements Runnable {

    private static final Map<String, Pattern> PATTERNS = Protocols.PATTERNS;

    Database database;
    Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean error = false;
    private User authenticatedUser = null;

    TCPChatServerSession(Database database, Socket socket) {
        this.database = database;
        this.socket = socket;
    }


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

        System.out.println("session " + this.socket + " ended.");
        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initInputOutput() throws IOException {
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void disconnect() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }

    private static class MatchSet {
        public String type;
        public Matcher matcher;
        MatchSet(String type, Matcher matcher) {
            this.type = type;
            this.matcher = matcher;
        }
    }

    private MatchSet findMatch(String message) {
        for (Map.Entry<String, Pattern> entry : PATTERNS.entrySet()) {
            Matcher matcher = entry.getValue().matcher(message);
            if(matcher.find())
                return new MatchSet(entry.getKey(), matcher);
        }
        return null;
    }

    private void handleMessage(String message) throws IOException {
        MatchSet matchSet = findMatch(message);
        if (matchSet != null) {
            switch (matchSet.type) {
                case "GET_RECENT":   getRecent(matchSet.matcher); break;
                case "GET_UNREAD":   getUnread(); break;
                case "POST_MESSAGE": postMessage(matchSet.matcher); break;
                case "LOGIN":        login(matchSet.matcher); break;
                case "REGISTER":     register(matchSet.matcher); break;
            }
        } else {
            sendError("Unknown request type.");
        }
    }

    private void login(Matcher matcher) throws IOException {
        System.out.println("login");
        String username = matcher.group("username");
        String password = matcher.group("password");
        if((authenticatedUser = database.login(username, password)) != null) {
            sendRespond("OK");
        } else {
            sendError("Wrong username or password.");
        }
    }

    private void register(Matcher matcher) throws IOException {
        String username = matcher.group("username");
        String fullName = matcher.group("fullName");
        String password = matcher.group("password");
        if(database.register(new User(username, fullName, password))) {
            sendRespond("OK");
        } else {
            sendError("This username is taken by other user.");
        }
    }

    private void postMessage(Matcher matcher) throws IOException {
        System.out.println("postMessage");
        if (authenticatedUser != null) {
            String message = matcher.group("message");
            database.addMessage(authenticatedUser.getUserName(), message);
            sendRespond("OK");
        } else {
            sendError("User is not authenticated.");
        }
    }

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

    private void getRecent(Matcher matcher) throws IOException {
        int numOfMsgs = Integer.parseInt(matcher.group("num"));
        sendRespond(formatMessages(database.readMessages(numOfMsgs)));
    }

    private String formatMessages(List<ChatMessage> messages) {
        String respond = "MESSAGES " + messages.size() + "\r\n";
        for (ChatMessage chatMessage : messages) {
            respond += "MESSAGE " + chatMessage.getUserName() +
                    " " + chatMessage.getTimestamp() + " "+chatMessage.getMessage() + "\r\n";
        }
        return respond;
    }

    private void sendError(String error) throws IOException {
        this.error = true;
        String errorMessage = "ERROR " + error + "\r\n";
        System.out.println(errorMessage);
        writer.write(errorMessage);
        writer.flush();
        socket.close();
    }

    private void sendRespond(String respond) throws IOException {
        writer.write(respond + "\r\n");
        writer.flush();
    }
}
