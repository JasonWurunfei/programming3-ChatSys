package programming3.chatsys.tcp;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.io.*;
import java.net.Socket;
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

    TCPChatServerSession(Database database, Socket socket) {
        this.database = database;
        this.socket = socket;
    }


    @Override
    public void run() {
        System.out.println("New session started at "+this.socket);
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = reader.readLine();
            System.out.println("Receive > "+message);
            handleMessage(message);
            writer.flush();
            disconnect();
            System.out.println("session "+this.socket+" ended");
        } catch (SocketTimeoutException e) {
            System.out.println("Socket timeout");
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        this.reader.close();
        this.writer.close();
        this.socket.close();
    }

    private Map.Entry<String, Pattern> findMatch(String message) {
        for (Map.Entry<String, Pattern> entry : PATTERNS.entrySet()) {
            if (Pattern.matches(entry.getValue().pattern(), message)) {
                return entry;
            }
        }
        return null;
    }

    private void handleMessage(String message) throws IOException {
        Map.Entry<String, Pattern> entry = findMatch(message);
        if (entry != null) {
            switch (entry.getKey()) {
                case "GET_RECENT": {
                    getRecent(message, entry);
                }
            }
        } else {
            System.out.println("Unknown request type.");
            writer.write("Unknown request type.");
        }
    }

    private void getRecent(String message, Map.Entry<String, Pattern> entry) throws IOException {
        Matcher matcher = entry.getValue().matcher(message);
        matcher.find();
        int numOfMsgs = Integer.parseInt(matcher.group(1));
        List<ChatMessage> messages = database.readMessages(numOfMsgs);
        
        String respond = "MESSAGES "+messages.size()+"\r\n";
        for (ChatMessage chatMessage : messages) {
            respond += "MESSAGE " + chatMessage.getUserName()+
                    " "+chatMessage.getTimestamp()+" "+chatMessage.getMessage() +"\r\n";
        }
        writer.write(respond);
    }
}
