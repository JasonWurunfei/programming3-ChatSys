package programming3.chatsys.tcp;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

/**
 * Represents a client used to connect to a TCP server
 * @author Wu Runfei (Jason SE181)
 */
public class TCPChatClient {

    /*
     * This class is similar to SimpleChatClient class in programming3.topic4.example5
     * (e.g. connect, send, disconnect methods)
     */

    private String serverHost;
    private int serverPort;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;

    TCPChatClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * Creates writer and reader for the interaction between server and clients.
     * @throws IOException if an I/O error occurs when creating the socket
     * input/output stream or client socket
     */
    public void connect() throws IOException {
        this.socket = initServerSocket();
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Release all the resources and close the socket
     * @throws IOException if an I/O error occurs when closing the resources.
     */
    public void disconnect() throws IOException {
        this.reader.close();
        this.writer.close();
        this.socket.close();
    }

    /**
     * Creates a client socket object
     * @throws IOException if an I/O error occurs when creating the socket
     */
    protected Socket initServerSocket() throws IOException {
        return new Socket(serverHost, serverPort);
    }

    /**
     * Sends a respond message to the client
     * @param message message that need to be sent
     * @throws IOException if an I/O error occurs.
     */
    public void send(String message) throws IOException {
        this.writer.write(message + "\r\n");
        this.writer.flush();
    }

    /**
     * request a certain number of messages from the server.
     * @param numOfMsgs number of the most recent messages will be returned.
     * @throws IOException if an I/O error occurs.
     */
    public void requestMessages(int numOfMsgs) throws IOException {
        this.send("{\"type\":\"getrecent\", \"n\":"+numOfMsgs+"}");
        String line = this.reader.readLine();
        System.out.println(line);
        JSONObject obj = new JSONObject(line);

        for (int i = 0; i < obj.getInt("n"); i++) {
            line = this.reader.readLine();
            System.out.println(line);
        }
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public static void main(String[] args) throws IOException {
        TCPChatClient client = new TCPChatClient("localhost", 1042);
        client.connect();
        client.requestMessages(10);
        client.disconnect();
    }
}
