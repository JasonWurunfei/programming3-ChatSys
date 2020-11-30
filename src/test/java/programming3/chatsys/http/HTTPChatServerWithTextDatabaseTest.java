package programming3.chatsys.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.SQLiteDatabase;
import programming3.chatsys.data.SecureTextDatabase;
import programming3.chatsys.data.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPChatServerWithTextDatabaseTest {

    SecureTextDatabase db;
    File userDB = new File(".\\user_test.db");
    File chatMessageDB = new File(".\\message_test.db");

    final int PORT = 8081;
    final String HOST = "localhost";
    HTTPChatServer server;
    Thread serverThread;
    int responseCode;

    ChatMessage cm1 = new ChatMessage(1, "user1", new Timestamp(100000), "Haloo");
    ChatMessage cm2 = new ChatMessage(2, "user_2", new Timestamp(200000), "Hello");

    public String HTTPRequest(String context, String method, String query) throws IOException {
        URL url = new URL("http://" + HOST + ":" + PORT + context);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        if (query != null) {
            connection.setDoOutput(true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(query);
            writer.flush();
            writer.close();
        }

        InputStream in;
        responseCode = connection.getResponseCode();
        if (responseCode < 400) { in = connection.getInputStream(); }
        else { in = connection.getErrorStream(); }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.readLine();
    }

    @BeforeEach
    void setUp() throws IOException {
        db = new SecureTextDatabase(chatMessageDB, userDB);
        cm1.save(chatMessageDB);
        cm2.save(chatMessageDB);
        server = new HTTPChatServer(db, PORT);
        serverThread = new Thread(() -> {
            server.start();
        });
        serverThread.start();
    }

    @AfterEach
    void tearDown(){
        server.stop();
        server = null;
        db = null;
        serverThread = null;

        if (chatMessageDB.exists())
            chatMessageDB.delete();
        if (userDB.exists())
            userDB.delete();
    }

    @Test
    void testGetRecent() throws IOException {
        String response = HTTPRequest("/recent/2", "GET", null);
        assertEquals("{\"messages\":[{\"id\":1,\"message\":\"Haloo\",\"username\":\"user1\",\"timestamp\":100000}," +
                "{\"id\":2,\"message\":\"Hello\",\"username\":\"user_2\",\"timestamp\":200000}]}", response);
        assertEquals(200, responseCode);

        response = HTTPRequest("/recent/100", "GET", null);
        assertEquals("{\"messages\":[{\"id\":1,\"message\":\"Haloo\",\"username\":\"user1\",\"timestamp\":100000}," +
                "{\"id\":2,\"message\":\"Hello\",\"username\":\"user_2\",\"timestamp\":200000}]}", response);
        assertEquals(200, responseCode);

        response = HTTPRequest("/recent/1", "GET", null);
        assertEquals("{\"messages\":[{\"id\":2,\"message\":\"Hello\",\"username\":\"user_2\",\"timestamp\":200000}]}", response);
        assertEquals(200, responseCode);
    }

    @Test
    void testGetRecentFailForInvalidN() throws IOException {
        String response = HTTPRequest("/recent/@", "GET", null);
        assertEquals("{\"error\":\"Number of messages in the URI is missing " +
                "or it cannot be parsed as a number\"}", response);
        assertEquals(400, responseCode);

        response = HTTPRequest("/recent/abc", "GET", null);
        assertEquals("{\"error\":\"Number of messages in the URI is missing " +
                "or it cannot be parsed as a number\"}", response);
        assertEquals(400, responseCode);

        response = HTTPRequest("/recent/-1", "GET", null);
        assertEquals("{\"error\":\"Request invalid number of messages\"}", response);
        assertEquals(400, responseCode);

        response = HTTPRequest("/recent/0", "GET", null);
        assertEquals("{\"error\":\"Request invalid number of messages\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testGetRecentFailForInvalidNoN() throws IOException {
        String response = HTTPRequest("/recent/", "GET", null);
        assertEquals("{\"error\":\"Number of messages in the URI is missing " +
                "or it cannot be parsed as a number\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testGetRecentFailForNotAllowedMethods() throws IOException {
        String response = HTTPRequest("/recent/2", "PUT", null);
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);

        response = HTTPRequest("/recent/2", "DELETE", null);
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);

        response = HTTPRequest("/recent/2", "POST", null);
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);
    }

    @Test
    void testUnreadMessage() throws IOException {
        String response = HTTPRequest("/unread/?username=user1&password=mypassword", "GET", null);
        assertEquals("{\"messages\":[{\"id\":1,\"message\":\"Haloo\",\"username\":\"user1\",\"timestamp\":100000}," +
                "{\"id\":2,\"message\":\"Hello\",\"username\":\"user_2\",\"timestamp\":200000}]}", response);
        assertEquals(200, responseCode);

        response = HTTPRequest("/unread/?username=user1&password=mypassword", "GET", null);
        assertEquals("{\"messages\":[]}", response);
        assertEquals(200, responseCode);
    }

    @Test
    void testUnreadMessageFailForInvalidUsernameOrPassword() throws IOException {
        String response = HTTPRequest("/unread/?username=user1&password=mypassword!", "GET", null);
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/unread/?username=user2&password=mypassword", "GET", null);
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/unread/?username=user1", "GET", null);
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/unread/?&password=mypassword", "GET", null);
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/unread/?&", "GET", null);
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/unread/?", "GET", null);
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);
    }

    @Test
    void testUnreadMessageFailForNoAuthenticationInfo() throws IOException {
        String response = HTTPRequest("/unread/", "GET", null);
        assertEquals("{\"error\":\"Authentication information not provided\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testUnreadMessageFailForNotAllowedMethods() throws IOException {
        String response = HTTPRequest("/unread/?username=user1&password=mypassword", "PUT", null);
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);

        response = HTTPRequest("/unread/?username=user1&password=mypassword", "DELETE", null);
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);

        response = HTTPRequest("/unread/?username=user1&password=mypassword", "POST", null);
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);
    }

    @Test
    void testRegister() throws IOException {
        String username = "john";
        String fullname = "John Doe";
        String password = "123456";
        String response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"type\":\"OK\"}", response);
        User user = db.readUsers().get(username);
        assertEquals(user.getUserName(), username);
        assertEquals(user.getFullName(), fullname);
        assertEquals(user.getPassword(), password);
        assertEquals(user.getLastReadId(), 0);
        assertEquals(201, responseCode);
    }

    @Test
    void testRegisterFailForNameAlreadyTaken() throws IOException {
        String username = "user1";
        String fullname = "John Doe";
        String password = "123456";
        String response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"This user name is taken\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testRegisterFailForIllegalUserData() throws IOException {
        String username = "use@r1";
        String fullname = "John Doe";
        String password = "123456";
        String response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"Illegal username, username can only use letters, numbers, and underscores\"}", response);
        assertEquals(400, responseCode);

        username = "user1";
        fullname = "John \\n Doe";
        password = "123456";
        response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"Illegal full name, fullName cannot contains a line feed\"}", response);
        assertEquals(400, responseCode);

        username = "user1";
        fullname = "John Doe";
        password = "1234\\n56";
        response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"Illegal password, password cannot contains a line feed\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testRegisterFailForMissingUserInfo() throws IOException {
        String fullname = "John Doe";
        String password = "123456";
        String response = HTTPRequest("/user/john", "POST",
                "{\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"username field not provided\"}", response);
        assertEquals(400, responseCode);

        String username = "john";
        password = "123456";
        response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"fullname field not provided\"}", response);
        assertEquals(400, responseCode);

        username = "user1";
        fullname = "John Doe";
        response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"}");
        assertEquals("{\"error\":\"password field not provided\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testRegisterFailForInvalidJSON() throws IOException {
        String username = "user1";
        String fullname = "John Doe";
        String password = "123456";
        String response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"");
        assertEquals("{\"error\":\"JSON syntax error: Expected a ',' or '}' " +
                "at 61 [character 62 line 1]\"}", response);
        assertEquals(400, responseCode);

        username = "user1";
        fullname = "John Doe";
        password = "123456";
        response = HTTPRequest("/user/john", "POST",
                "{\"username\":\"" + username + "\"" +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"JSON syntax error: Expected a ',' or '}' " +
                "at 20 [character 21 line 1]\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testRegisterFailForNoUserInfo() throws IOException {
        String response = HTTPRequest("/user/john", "POST", null);
        assertEquals("{\"error\":\"Missing user information\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testRegisterFailForNotAllowedMethods() throws IOException {
        String username = "john";
        String fullname = "John Doe";
        String password = "123456";
        String response = HTTPRequest("/user/john", "GET", null);
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        response = HTTPRequest("/user/john", "DELETE",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);

        response = HTTPRequest("/user/john", "PUT",
                "{\"username\":\"" + username + "\"," +
                        "\"fullname\":\"" + fullname + "\"," +
                        "\"password\":\"" + password + "\"}");
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);
    }

    @Test
    void testPostMessage() throws IOException {
        String response = HTTPRequest("/message/?username=user_2&password=PassWord", "POST",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"type\":\"OK\"}", response);
        List<ChatMessage> messages = db.readMessages();
        ChatMessage message = messages.get(messages.size()-1);
        assertEquals("user_2", message.getUserName());
        assertEquals("Hello world!", message.getMessage());
        assertEquals(201, responseCode);
    }

    @Test
    void testPostMessageFailForInvalidUsernameOrPassword() throws IOException {
        String response = HTTPRequest("/message/?username=user_2&password=PassWord1", "POST",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/message/?username=user_3&password=PassWord", "POST",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/message/?username=user_2", "POST",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/message/?password=PassWord", "POST",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/message/?&", "POST",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);

        response = HTTPRequest("/message/?", "POST",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"error\":\"Invalid username or password\"}", response);
        assertEquals(401, responseCode);
    }

    @Test
    void testPostMessageFailForJSONSyntaxError() throws IOException {
        String response = HTTPRequest("/message/?username=user_2&password=PassWord", "POST",
                "{\"message\":\"Hello world!\"");
        assertEquals("{\"error\":\"JSON syntax error: Expected a ',' or '}' " +
                "at 25 [character 26 line 1]\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testPostMessageFailForNoAuthenticationInfo() throws IOException {
        String response = HTTPRequest("/message/", "POST",
                "{\"message\":\"Hello world!\"");
        assertEquals("{\"error\":\"Authentication information not provided\"}", response);
        assertEquals(400, responseCode);
    }

    @Test
    void testPostMessageFailForNotAllowedMethods() throws IOException {
        String response = HTTPRequest("/message/?username=user_2&password=PassWord", "GET",
                null);
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);

        response = HTTPRequest("/message/?username=user_2&password=PassWord", "DELETE",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);

        response = HTTPRequest("/message/?username=user_2&password=PassWord", "PUT",
                "{\"message\":\"Hello world!\"}");
        assertEquals("{\"error\":\"Method not allowed\"}", response);
        assertEquals(405, responseCode);
    }

    @Test
    void testUnknownRequest() throws IOException {
        String response = HTTPRequest("/2", "GET", null);
        assertEquals("<h1>404 Not Found</h1>No context found for request", response);
        assertEquals(404, responseCode);

        response = HTTPRequest("/abc", "GET", null);
        assertEquals("<h1>404 Not Found</h1>No context found for request", response);
        assertEquals(404, responseCode);

        response = HTTPRequest("/hello/world", "GET", null);
        assertEquals("<h1>404 Not Found</h1>No context found for request", response);
        assertEquals(404, responseCode);
    }
}