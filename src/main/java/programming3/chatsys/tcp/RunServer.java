package programming3.chatsys.tcp;

import programming3.chatsys.data.SecureTextDatabase;

import java.io.File;
import java.io.IOException;

public class RunServer {
    public static void run(String host, int port, int timeout, String messagesDb, String userDb) throws IOException {
        SecureTextDatabase db = new SecureTextDatabase(
                new File(messagesDb),
                new File(userDb)
        );
        TCPChatServer server = new TCPChatServer(port, timeout, db);
        server.start();
    }

    public static void main(String[] args) throws IOException {
        RunServer.run("localhost", 1042, 100000,
                ".\\data\\messages.db", ".\\data\\user.db");
    }
}
