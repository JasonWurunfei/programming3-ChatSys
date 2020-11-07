package programming3.chatsys.tcp;

import programming3.chatsys.data.Database;
import programming3.chatsys.data.SecureTextDatabase;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Represents a TCP server
 * @author Wu Runfei (Jason SE181)
 */
public class TCPChatServer {
    private int port;
    private int timeout;
    private Database database;
    private Boolean isRunning = false;
    private ServerSocket socket;

    public TCPChatServer(int port, int timeout, Database database) {
        this.port = port;
        this.timeout = timeout;
        this.database = database;
    }

    /**
     * Starts the server
     */
    public void start() throws IOException {
        ExecutorService exec = Executors.newCachedThreadPool();
        this.socket = initServerSocket();
        isRunning = true;
        System.out.println("System started and ready for connections ...");

        while (isRunning) {
            try {
                Socket clientSocket = this.socket.accept();
                clientSocket.setSoTimeout(timeout);
                exec.submit(new TCPChatServerSession(this.database, clientSocket));
            } catch (SocketException e) {
            }
        }

        System.out.println("System shutdown in one seconds");
        exec.shutdown();
        try {
            if(!exec.awaitTermination(1, TimeUnit.SECONDS))
                exec.shutdownNow();
        } catch (InterruptedException ignore) {
            exec.shutdownNow();
        }
        System.out.println("System shutdown");
    }

    /**
     * Creates a server socket object
     * @throws IOException if an I/O error occurs when creating the socket
     */
    protected ServerSocket initServerSocket() throws IOException {
        return new ServerSocket(port);
    }

    /**
     * Stops the server
     */
    public void stop() throws IOException {
        isRunning = false;
        socket.close();
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) throws IOException {
        Database db = new SecureTextDatabase(
                new File(".\\data\\messages.db"),
                new File(".\\data\\user.db")
        );
        TCPChatServer server = new TCPChatServer(1042, 100000, db);
        server.start();
    }
}
