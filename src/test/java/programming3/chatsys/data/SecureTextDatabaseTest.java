package programming3.chatsys.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SecureTextDatabaseTest {

    SecureTextDatabase db;
    File chatMessageDB = new File(".\\message_test.db");
    File userDB = new File(".\\user_test.db");
    ChatMessage cm1 = new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo");
    ChatMessage cm2 = new ChatMessage(2, "Ana", new Timestamp(200000), "Hello");
    User user1 = new User("Jack", "JackMa", "666666");
    User user2 = new User("Jason", "JasonWu", "123456", 1);
    User defaultUser1 = new User("user1\tUser1\tmypassword\t0");
    User defaultUser2 = new User("user_2\tFull Name\tPassword\t0");

    @BeforeEach
    void setUp() {
        db = new SecureTextDatabase(chatMessageDB, userDB);

        cm1.save(chatMessageDB);
        cm2.save(chatMessageDB);

        user1.save(userDB);
        user2.save(userDB);
    }

    @AfterEach
    void tearDown() {
        db = null;
        chatMessageDB.delete();
        userDB.delete();
    }

    @Test
    void readMessages() {
        ChatMessage[] msgArray = new ChatMessage[] {
                new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello"),
        };
        List<ChatMessage> msgList = Arrays.asList(msgArray);
        assertEquals(msgList, db.readMessages());
    }

    @Test
    void readUsers() {
        Map<String, User> userMap = new HashMap<String, User>();
        userMap.put("Jack", user1);
        userMap.put("Jason", user2);
        userMap.put("user1", defaultUser1);
        userMap.put("user_2", defaultUser2);
        assertEquals(userMap, db.readUsers());
    }

    @Test
    void addMessage() {
        ChatMessage cm = new ChatMessage(5, "Jack", new Timestamp(1000000), "Hello World");
        db.addMessage(cm);

        ChatMessage[] msgArray = new ChatMessage[] {
                new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello"),
                new ChatMessage(5, "Jack", new Timestamp(1000000), "Hello World")
        };
        List<ChatMessage> msgList2 = Arrays.asList(msgArray);

        assertEquals(msgList2, db.readMessages());
    }

    @Test
    void getUnreadMessages() {
        ChatMessage[] msgArray = new ChatMessage[] {
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello"),
        };
        List<ChatMessage> msgList = Arrays.asList(msgArray);
        assertEquals(msgList, db.getUnreadMessages("Jason"));

        msgArray = new ChatMessage[] {
                new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello"),
        };
        msgList = Arrays.asList(msgArray);
        assertEquals(msgList, db.getUnreadMessages("Jack"));

        Map<String, User> userMap = db.readUsers();
        assertEquals(2, userMap.get("Jack").getLastReadId());
        assertEquals(2, userMap.get("Jason").getLastReadId());
    }

    @Test
    void alreadyRegistered() {
        User user1 = new User("Jack", "JackMa", "666666");
        assertFalse(db.register(user1));
    }

    @Test
    void registerSuccess() {
        User user1 = new User("Jack_123", "JackMa", "666666");
        assertTrue(db.register(user1));
    }
}