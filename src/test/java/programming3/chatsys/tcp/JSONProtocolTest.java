package programming3.chatsys.tcp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.User;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class JSONProtocolTest {

    JSONProtocol protocol;
    User user = new User("john", "John Doe", "ThePassword");
    ChatMessage message = new ChatMessage(
            0, "johndoe", new Timestamp(1604656487868L),"This is a chat message"
    );

    @BeforeEach
    void setUp() {
        protocol = new JSONProtocol(new StringWriter());
    }

    @AfterEach
    void tearDown() {
        protocol = null;
    }

    @Test
    void formatUser() {
        JSONObject obj = protocol.formatUser("john", "John Doe", "ThePassword");
        assertEquals("john", obj.getString("username"));
        assertEquals("John Doe", obj.getString("fullname"));
        assertEquals("ThePassword", obj.getString("password"));
    }

    @Test
    void testFormatUser() {
        JSONObject obj = protocol.formatUser(user);
        assertEquals("john", obj.getString("username"));
        assertEquals("John Doe", obj.getString("fullname"));
        assertEquals("ThePassword", obj.getString("password"));
    }

    @Test
    void parseUser() {
        JSONObject obj = new JSONObject(
                "{\"fullname\":\"John Doe\",\"password\":\"ThePassword\",\"username\":\"john\"}");
        User parsedUser = protocol.parseUser(obj);
        assertEquals(user, parsedUser);
    }

    @Test
    void parseUserFailForNoUsername() {
        JSONObject obj = new JSONObject("{\"fullname\":\"John Doe\",\"password\":\"ThePassword\"}");
        assertThrows(JSONException.class, () -> protocol.parseUser(obj));
    }

    @Test
    void parseUserFailForNoPassword() {
        JSONObject obj = new JSONObject("{\"fullname\":\"John Doe\",\"username\":\"john\"}");
        assertThrows(JSONException.class, () -> protocol.parseUser(obj));
    }

    @Test
    void parseUserFailForNoFullName() {
        JSONObject obj = new JSONObject("{\"password\":\"ThePassword\",\"username\":\"john\"}");
        assertThrows(JSONException.class, () -> protocol.parseUser(obj));
    }

    @Test
    void writeUser() throws IOException {
        protocol.writeUser(user);
        JSONObject obj = new JSONObject(protocol.getWriter().toString());
        assertEquals("john", obj.getString("username"));
        assertEquals("John Doe", obj.getString("fullname"));
        assertEquals("ThePassword", obj.getString("password"));
    }

    @Test
    void formatChatMessage() {
        JSONObject obj = protocol.formatChatMessage(
                0, "This is a chat message", "johndoe", 1604656487868L);
        assertEquals(0, obj.getInt("id"));
        assertEquals("johndoe", obj.getString("username"));
        assertEquals(1604656487868L, obj.getLong("timestamp"));
        assertEquals("This is a chat message", obj.getString("message"));
    }

    @Test
    void testFormatChatMessage() {
        JSONObject obj = protocol.formatChatMessage(message);
        assertEquals(0, obj.getInt("id"));
        assertEquals("johndoe", obj.getString("username"));
        assertEquals(1604656487868L, obj.getLong("timestamp"));
        assertEquals("This is a chat message", obj.getString("message"));
    }

    @Test
    void parseChatMessage() {
        JSONObject obj = new JSONObject(
                "{\"id\":0,\"message\":\"This is a chat message\"," +
                        "\"username\":\"johndoe\",\"timestamp\":1604656487868}");
        ChatMessage parsedMessage = protocol.parseChatMessage(obj);
        assertEquals(message.getId(), parsedMessage.getId());
        assertEquals(message.getMessage(), parsedMessage.getMessage());
        assertEquals(message.getUserName(), parsedMessage.getUserName());
        assertEquals(message.getTimestamp(), parsedMessage.getTimestamp());
    }

    @Test
    void parseChatMessageFailForNoId() {
        JSONObject obj = new JSONObject("{\"message\":\"This is a chat message\"," +
                "\"username\":\"johndoe\",\"timestamp\":1604656487868}");
        assertThrows(JSONException.class, () -> protocol.parseChatMessage(obj));
    }

    @Test
    void parseChatMessageFailForNoMessage() {
        JSONObject obj = new JSONObject("{\"id\":0,\"username\":\"johndoe\"," +
                "\"timestamp\":1604656487868}");
        assertThrows(JSONException.class, () -> protocol.parseChatMessage(obj));
    }

    @Test
    void parseChatMessageFailForNoUsername() {
        JSONObject obj = new JSONObject("{\"id\":0,\"message\":\"This is a chat message\"," +
                "\"timestamp\":1604656487868}");
        assertThrows(JSONException.class, () -> protocol.parseChatMessage(obj));
    }

    @Test
    void parseChatMessageFailForNoTimestamp() {
        JSONObject obj = new JSONObject("{\"id\":0,\"message\":\"This is a chat message\"," +
                "\"username\":\"johndoe\"}");
        assertThrows(JSONException.class, () -> protocol.parseChatMessage(obj));
    }

    @Test
    void writeChatMessage() throws IOException {
        protocol.writeChatMessage(message);
        JSONObject obj = new JSONObject(protocol.getWriter().toString());
        assertEquals(0, obj.getInt("id"));
        assertEquals("johndoe", obj.getString("username"));
        assertEquals(1604656487868L, obj.getLong("timestamp"));
        assertEquals("This is a chat message", obj.getString("message"));
    }
}