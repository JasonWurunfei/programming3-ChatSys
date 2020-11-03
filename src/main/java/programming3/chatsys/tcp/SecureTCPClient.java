package programming3.chatsys.tcp;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class SecureTCPClient extends TCPChatClient{
    private String serverHost;
    private int serverPort;
    private Socket socket;

    SecureTCPClient(String serverHost, int serverPort) throws IOException {
        super(serverHost, serverPort);
    }

    @Override
    public void initServerSocket() throws IOException {
        SocketFactory factory = SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket(serverHost, serverPort);
        String[] supported = socket.getSupportedCipherSuites();
        socket.setEnabledCipherSuites(supported);
        this.socket = socket;
    }
}
