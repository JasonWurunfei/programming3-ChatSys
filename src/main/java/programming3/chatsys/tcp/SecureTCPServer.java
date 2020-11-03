package programming3.chatsys.tcp;

import programming3.chatsys.data.Database;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class SecureTCPServer extends TCPChatServer {
    private int port;
    private ServerSocket socket;

    public SecureTCPServer(int port, int timeout, Database database) {
        super(port, timeout, database);
    }

    @Override
    public void initServerSocket() throws IOException {
        try {
            SSLContext context = SSLContext.getInstance("TLSv1.2");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = System.console().readPassword();
            ks.load(new FileInputStream("mykeys.keys"), password);
            kmf.init(ks, password);
            context.init(kmf.getKeyManagers(), null, null);
            Arrays.fill(password, '0');
            SSLServerSocketFactory factory = context.getServerSocketFactory();
            SSLServerSocket socket = (SSLServerSocket) factory.createServerSocket(port);
            socket.setEnableSessionCreation(true);
            this.socket = socket;
        } catch (NoSuchAlgorithmException |
                 KeyStoreException |
                 CertificateException |
                 UnrecoverableKeyException |
                 KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
