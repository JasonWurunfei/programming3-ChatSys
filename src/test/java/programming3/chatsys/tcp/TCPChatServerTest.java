package programming3.chatsys.tcp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.SecureTextDatabase;
import programming3.chatsys.data.User;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TCPChatServerTest {

    Socket client;
    Thread serverThread;
    TCPChatServer server;
    SecureTextDatabase db;
    BufferedWriter writer;
    BufferedReader reader;
    File userDB = new File(".\\user_test.db");
    File chatMessageDB = new File(".\\message_test.db");
    final int PORT = 1040;
    final String HOST = "localhost";

    private void send(String message) throws IOException {
        writer.write(message + "\r\n");
        writer.flush();
    }

    @BeforeEach
    void setUp() throws IOException {
        db = new SecureTextDatabase(chatMessageDB, userDB);
        server = new TCPChatServer(PORT, 0, db);
        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        client = new Socket(HOST, PORT);
        writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    @AfterEach
    void tearDown() throws IOException {
        server.stop();
        server = null;
        db = null;
        serverThread = null;

        if (chatMessageDB.exists())
            chatMessageDB.delete();
        if (userDB.exists())
            userDB.delete();

        writer.close();
        client.close();
    }

    @Test
    @Timeout(10000)
    void testLoginSuccess() throws IOException {
        send("LOGIN user1 mypassword");
        assertEquals("OK", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testLoginFailForIncorrectPassword() throws IOException {
        send("LOGIN user1 mypassword1");
        assertEquals("ERROR Wrong username or password.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testLoginFailForIncorrectUsername() throws IOException {
        send("LOGIN user mypassword");
        assertEquals("ERROR Wrong username or password.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testRegisterSuccess() throws IOException {
        send("REGISTER user user_1 123456");
        assertEquals("OK", reader.readLine());
        User user = db.readUsers().get("user");
        assertNotNull(user);
        assertEquals("user", user.getUserName());
        assertEquals("user_1", user.getFullName());
        assertEquals("123456", user.getPassword());
    }

    @Test
    @Timeout(10000)
    void testRegisterFailForInvalidUsername1() throws IOException {
        send("REGISTER user@ user_1 123456");
        assertEquals("ERROR userName is invalid.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testRegisterFailForInvalidUsername2() throws IOException {
        send("REGISTER 吴润飞 user_1 123456");
        assertEquals("ERROR userName is invalid.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testRegisterFailForInvalidFullName() throws IOException {
        send("REGISTER user use\nr_1 123456");
        assertEquals("ERROR Unknown request type.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testPostMessagesSuccess() throws IOException {
        // login first
        send("LOGIN user1 mypassword");
        assertEquals("OK", reader.readLine());

        // send messages
        String message = "HAHAHA";
        send("POST 1"+ message);
        assertEquals("OK", reader.readLine());
        send("POST 2"+ message);
        assertEquals("OK", reader.readLine());

        ChatMessage cm1 = db.readMessages().get(0);
        assertEquals(1 , cm1.getId());
        assertEquals("1" + message , cm1.getMessage());
        assertEquals("user1" , cm1.getUserName());

        ChatMessage cm2 = db.readMessages().get(1);
        assertEquals(2 , cm2.getId());
        assertEquals("2" + message , cm2.getMessage());
        assertEquals("user1" , cm2.getUserName());
    }

    @Test
    @Timeout(10000)
    void testPostMessagesFailForNotAuthenticated() throws IOException {
        // send messages
        String message = "HAHAHA";
        send("POST "+ message);
        assertEquals("ERROR User is not authenticated.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetUnreadSuccess() throws IOException, InterruptedException {
        // login first
        send("LOGIN user1 mypassword");
        assertEquals("OK", reader.readLine());

        send("GET unread messages");
        assertEquals("MESSAGES 0", reader.readLine());

        // add unread messages
        new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo").save(chatMessageDB);
        new ChatMessage(2, "Ana", new Timestamp(200000), "Hello").save(chatMessageDB);
        Thread.sleep(100);
        send("GET unread messages");

        assertEquals("MESSAGES 2", reader.readLine());
        assertEquals("MESSAGE Jack_1 " + new Timestamp(100000) + " Haloo", reader.readLine());
        assertEquals("MESSAGE Ana " + new Timestamp(200000) + " Hello", reader.readLine());

        send("GET unread messages");
        assertEquals("MESSAGES 0", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetUnreadFailForNotAuthenticated() throws IOException {
        send("GET unread messages");
        assertEquals("ERROR User is not authenticated.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetRecentSuccess() throws IOException, InterruptedException {
        send("GET recent messages 10");
        assertEquals("MESSAGES 0", reader.readLine());

        // add unread messages
        new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo").save(chatMessageDB);
        new ChatMessage(2, "Ana", new Timestamp(200000), "Hello").save(chatMessageDB);
        Thread.sleep(100);

        send("GET recent messages 1");
        assertEquals("MESSAGES 1", reader.readLine());
        assertEquals("MESSAGE Ana " + new Timestamp(200000) + " Hello", reader.readLine());

        send("GET recent messages 2");
        assertEquals("MESSAGES 2", reader.readLine());
        assertEquals("MESSAGE Jack_1 " + new Timestamp(100000) + " Haloo", reader.readLine());
        assertEquals("MESSAGE Ana " + new Timestamp(200000) + " Hello", reader.readLine());

        send("GET recent messages 10");
        assertEquals("MESSAGES 2", reader.readLine());
        assertEquals("MESSAGE Jack_1 " + new Timestamp(100000) + " Haloo", reader.readLine());
        assertEquals("MESSAGE Ana " + new Timestamp(200000) + " Hello", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetRecentFailForInvalidNum1() throws IOException {
        send("GET recent messages -1");
        assertEquals("ERROR Invalid argument -1.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetRecentFailForInvalidNum2() throws IOException {
        send("GET recent messages abc");
        assertEquals("ERROR Invalid argument abc.", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetRecentFailForInvalidNum3() throws IOException {
        send("GET recent messages @#$%^");
        assertEquals("ERROR Invalid argument @#$%^.", reader.readLine());
    }

}