package programming3.chatsys.tcp;

import programming3.chatsys.data.Database;
import programming3.chatsys.data.SecureTextDatabase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPChatServer {
    private int port;
    private int timeout;
    private Database database;
    private Boolean isRunning = false;
    private ServerSocket socket;

    public int getPort() {
        return port;
    }

    public TCPChatServer(int port, int timeout, Database database) {
        this.port = port;
        this.timeout = timeout;
        this.database = database;
    }

    public void start() throws IOException {
        ExecutorService exec = Executors.newCachedThreadPool();
        this.socket = initServerSocket();
        isRunning = true;
        System.out.println("System started");

        while (isRunning) {
            Socket clientSocket = this.socket.accept();
            System.out.println("New connection from " + clientSocket);
            clientSocket.setSoTimeout(timeout);
            exec.submit(new TCPChatServerSession(this.database, clientSocket));
        }

        System.out.println("System shutdown in two seconds");
        exec.shutdown();
        try {
            if(!exec.awaitTermination(2, TimeUnit.SECONDS))
                exec.shutdownNow();
        } catch (InterruptedException ignore) {
            exec.shutdownNow();
        }
        stop();
        System.out.println("System shutdown");
    }

    protected ServerSocket initServerSocket() throws IOException {
        return new ServerSocket(port);
    }

    public void stop() throws IOException {
        isRunning = false;
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        Database db = new SecureTextDatabase();
        TCPChatServer server = new TCPChatServer(1042, 100000, db);
        server.start();
    }

}
