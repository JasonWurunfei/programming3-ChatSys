package programming3.chatsys.tcp;

import programming3.chatsys.data.Database;
import programming3.chatsys.data.SecureTextDatabase;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class SecureTCPServer extends TCPChatServer {

    public SecureTCPServer(int port, int timeout, Database database) {
        super(port, timeout, database);
    }

    @Override
    public ServerSocket initServerSocket() throws IOException {
        // referencing from
        // https://moodle.njit.edu.cn/moodle/pluginfile.php/5740/mod_folder/content/0/04-03%20-%20SSL.pdf?forcedownload=1
        SSLServerSocket socket = null;
        try {
            // Create an SSLContext for the SSL/TLS algorithm version
            SSLContext context = SSLContext.getInstance("TLSv1.2");

            // Create a TrustManagerFactory for verifying the certificate
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            // Create a KeyStore object for the key and certificate database
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            // To demonstrate, I used hardcoded password here.
            char[] password = {'1','2','3','4','5','6'};

            // Fill the KeyStore object with keys and certificates
            ks.load(new FileInputStream("mykeys.keys"), password);

            //Initialize the KeyManagerFactory with the KeyStore and the password
            kmf.init(ks, password);

            //Initialize the context with the necessary key managers, trust managers, and a source of randomness.
            context.init(kmf.getKeyManagers(), null, null);

            // Wipe the password from memory
            Arrays.fill(password, '0');

            // Get an SSLServerSocket with the factory
            SSLServerSocketFactory factory = context.getServerSocketFactory();
            socket = (SSLServerSocket) factory.createServerSocket(this.getPort());

            // Allow SSL sessions to be reused across multiple connections
            socket.setEnableSessionCreation(true);

        } catch (NoSuchAlgorithmException |
                 KeyStoreException |
                 CertificateException |
                 UnrecoverableKeyException |
                 KeyManagementException e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static void main(String[] args) throws IOException {
        Database db = new SecureTextDatabase();
        SecureTCPServer server = new SecureTCPServer(1042, 100000, db);
        server.start();
    }
}
