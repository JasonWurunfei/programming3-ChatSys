package programming3.chatsys.tcp;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPChatServerSession implements Runnable {

    private static final Pattern GET_RECENT_MSG_PATTERN =
            Pattern.compile("^GET recent messages ([\\d]+)$");

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
            writer.close();
            reader.close();
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

    private void handleMessage(String message) throws IOException {
        Matcher matcher = GET_RECENT_MSG_PATTERN.matcher(message);
        if (matcher.find()) {
            int numOfMsgs = Integer.parseInt(matcher.group(1));
            List<ChatMessage> messages = database.readMessages();
            int numOfMsgsInDB = messages.size();
            messages = messages.subList(messages.size()-numOfMsgs, messages.size());

            String respond = "MESSAGES "+Math.min(numOfMsgsInDB, numOfMsgs)+"\r\n";
            for (ChatMessage chatMessage : messages) {
                respond += "MESSAGE " + chatMessage.getUserName()+
                        " "+chatMessage.getTimestamp()+" "+chatMessage.getMessage() +"\r\n";
            }

            writer.write(respond);
        } else {
            System.out.println("Unknow request type.");
            writer.write("Unknown request type.");
        }
    }
}
