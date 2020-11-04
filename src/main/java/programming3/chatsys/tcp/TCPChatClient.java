package programming3.chatsys.tcp;

import java.io.*;
import java.net.Socket;


public class TCPChatClient {

    /*
     * The way this Client class is implemented is partially referring
     * the SimpleChatClient class in programming3.topic4.example5
     * (e.g. connect, send, disconnect methods)
     */

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    private String serverHost;
    private int serverPort;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;

    TCPChatClient(String serverHost, int serverPort) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        this.socket = initServerSocket();
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    protected Socket initServerSocket() throws IOException {
        return new Socket(serverHost, serverPort);
    }

    public void send(String message) throws IOException {
        this.writer.write(message + "\r\n");
        this.writer.flush();
    }

    public void requestMessages(int numOfMsg) throws IOException {
        this.send("GET recent messages "+numOfMsg);
        String line;
        while ((line = this.reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    public void disconnect() throws IOException {
        this.reader.close();
        this.writer.close();
        this.socket.close();
    }

    public static void main(String[] args) throws IOException {
        TCPChatClient client = new TCPChatClient("localhost", 1042);
        client.connect();
        client.requestMessages(10);
        client.disconnect();
    }
}
