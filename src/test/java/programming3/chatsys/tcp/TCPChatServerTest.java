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
        send("{\"type\":\"login\", \"username\":\"user1\",\"password\":\"mypassword\"}");
        assertEquals("{\"type\":\"ok\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testLoginFailForIncorrectPassword() throws IOException {
        send("{\"type\":\"login\", \"username\":\"user1\",\"password\":\"mypassword1\"}");
        assertEquals("{\"type\":\"error\",\"message\":\"Wrong username or password.\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testLoginFailForIncorrectUsername() throws IOException {
        send("{\"type\":\"login\", \"username\":\"user\",\"password\":\"mypassword\"}");
        assertEquals("{\"type\":\"error\",\"message\":\"Wrong username or password.\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testRegisterSuccess() throws IOException {
        send("{\"type\":\"register\", \"username\":\"user\",\"fullname\":\"user_1\",\"password\":\"123456\"}");
        assertEquals("{\"type\":\"ok\"}", reader.readLine());
        User user = db.readUsers().get("user");
        assertNotNull(user);
        assertEquals("user", user.getUserName());
        assertEquals("user_1", user.getFullName());
        assertEquals("123456", user.getPassword());
    }

    @Test
    @Timeout(10000)
    void testRegisterFailForUsernameIsTaken() throws IOException {
        send("{\"type\":\"register\", \"username\":\"user1\",\"fullname\":\"user_1\",\"password\":\"123456\"}");
        assertEquals("{\"type\":\"error\",\"message\":\"register user user1 failed. " +
                "This username is taken by other user.\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testRegisterFailForInvalidCharacterInUsername() throws IOException {
        send("{\"type\":\"register\", \"username\":\"user@\",\"fullname\":\"user_1\",\"password\":\"123456\"}");
        assertEquals("{\"type\":\"error\",\"message\":\"userName is invalid.\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testRegisterFailForChineseUsername() throws IOException {
        send("{\"type\":\"register\", \"username\":\"吴润飞\",\"fullname\":\"user_1\",\"password\":\"123456\"}");
        assertEquals("{\"type\":\"error\",\"message\":\"userName is invalid.\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testRegisterFailForInvalidFullName() throws IOException {
        send("{\"type\":\"register\", \"username\":\"user\",\"fullname\":\"user\n_1\",\"password\":\"123456\"}");
        assertEquals("{\"type\":\"error\",\"message\":\"request must be in JSON format.\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testPostMessagesSuccess() throws IOException {
        // login first
        send("{\"type\":\"login\", \"username\":\"user1\",\"password\":\"mypassword\"}");
        assertEquals("{\"type\":\"ok\"}", reader.readLine());

        // send messages
        String message = "HAHAHA";
        send("{\"type\":\"post\", \"message\":\"1"+message+"\"}");
        assertEquals("{\"type\":\"ok\"}", reader.readLine());
        send("{\"type\":\"post\", \"message\":\"2"+message+"\"}");
        assertEquals("{\"type\":\"ok\"}", reader.readLine());

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
        send("{\"type\":\"post\", \"message\":\""+message+"\"}");
        assertEquals("{\"type\":\"error\",\"message\":\"User is not authenticated.\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetUnreadSuccess() throws IOException, InterruptedException {
        // login first
        send("{\"type\":\"login\", \"username\":\"user1\",\"password\":\"mypassword\"}");
        assertEquals("{\"type\":\"ok\"}", reader.readLine());

        send("{\"type\":\"getunread\"}");
        assertEquals("{\"type\":\"messages\",\"n\":0}", reader.readLine());

        // add unread messages
        new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo").save(chatMessageDB);
        new ChatMessage(2, "Ana", new Timestamp(200000), "Hello").save(chatMessageDB);
        Thread.sleep(100);
        send("{\"type\":\"getunread\"}");

        assertEquals("{\"type\":\"messages\",\"n\":2}", reader.readLine());
        assertEquals("{\"type\":\"message\",\"message\":" +
                "{\"id\":1,\"message\":\"Haloo\",\"username\":\"Jack_1\",\"timestamp\":100000}}", reader.readLine());
        assertEquals("{\"type\":\"message\",\"message\":" +
                "{\"id\":2,\"message\":\"Hello\",\"username\":\"Ana\",\"timestamp\":200000}}", reader.readLine());

        send("{\"type\":\"getunread\"}");
        assertEquals("{\"type\":\"messages\",\"n\":0}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetUnreadFailForNotAuthenticated() throws IOException {
        send("{\"type\":\"getunread\"}");
        assertEquals("{\"type\":\"error\",\"message\":\"User is not authenticated.\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetRecentSuccess() throws IOException, InterruptedException {
        send("{\"type\":\"getrecent\", \"n\":10}");
        assertEquals("{\"type\":\"messages\",\"n\":0}", reader.readLine());

        // add unread messages
        new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo").save(chatMessageDB);
        new ChatMessage(2, "Ana", new Timestamp(200000), "Hello").save(chatMessageDB);
        Thread.sleep(100);

        send("{\"type\":\"getrecent\", \"n\":1}");
        assertEquals("{\"type\":\"messages\",\"n\":1}", reader.readLine());
        assertEquals("{\"type\":\"message\",\"message\":{\"id\":2,\"message\":" +
                "\"Hello\",\"username\":\"Ana\",\"timestamp\":200000}}", reader.readLine());

        send("{\"type\":\"getrecent\", \"n\":2}");
        assertEquals("{\"type\":\"messages\",\"n\":2}", reader.readLine());
        assertEquals("{\"type\":\"message\",\"message\":{\"id\":1,\"message\":" +
                "\"Haloo\",\"username\":\"Jack_1\",\"timestamp\":100000}}", reader.readLine());
        assertEquals("{\"type\":\"message\",\"message\":{\"id\":2,\"message\":" +
                "\"Hello\",\"username\":\"Ana\",\"timestamp\":200000}}", reader.readLine());

        send("{\"type\":\"getrecent\", \"n\":10}");
        assertEquals("{\"type\":\"messages\",\"n\":2}", reader.readLine());
        assertEquals("{\"type\":\"message\",\"message\":{\"id\":1,\"message\":" +
                "\"Haloo\",\"username\":\"Jack_1\",\"timestamp\":100000}}", reader.readLine());
        assertEquals("{\"type\":\"message\",\"message\":{\"id\":2,\"message\":" +
                "\"Hello\",\"username\":\"Ana\",\"timestamp\":200000}}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetRecentFailForInvalidNum1() throws IOException {
        send("{\"type\":\"getrecent\", \"n\":-1}");
        assertEquals("{\"type\":\"error\",\"message\":" +
                "\"request 0 or invalid number of messages\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetRecentFailForInvalidNum2() throws IOException {
        send("{\"type\":\"getrecent\", \"n\":abc}");
        assertEquals("{\"type\":\"error\",\"message\":" +
                "\"request 0 or invalid number of messages\"}", reader.readLine());
    }

    @Test
    @Timeout(10000)
    void testGetRecentFailForInvalidNum3() throws IOException {
        send("{\"type\":\"getrecent\", \"n\":@#$%^}");
        assertEquals("{\"type\":\"error\",\"message\":" +
                "\"request must be in JSON format.\"}", reader.readLine());
    }

}