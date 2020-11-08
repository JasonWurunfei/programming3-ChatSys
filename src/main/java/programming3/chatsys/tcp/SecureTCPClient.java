package programming3.chatsys.tcp;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

public class SecureTCPClient extends TCPChatClient{

    SecureTCPClient(String serverHost, int serverPort) {
        super(serverHost, serverPort);
    }

    /**
     * Creates a SSL client socket object
     * @throws IOException if an I/O error occurs when creating the socket
     */
    @Override
    public Socket initServerSocket() throws IOException {
        SocketFactory factory = SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket(getServerHost(), getServerPort());
        String[] supported = socket.getSupportedCipherSuites();
        socket.setEnabledCipherSuites(supported);
        return socket;
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("javax.net.ssl.trustStore", "mykeys.keys");
        SecureTCPClient client = new SecureTCPClient("localhost", 1042);
        client.connect();
        client.requestMessages(10);
        client.disconnect();
    }
}
