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

    @BeforeEach
    void setUp() {
        db = new SecureTextDatabase();
        db.setChatMessageDB(new File(".\\chatMessage_secureDatabase_test.txt"));
        db.setUserDB(new File(".\\user_secureDatabase_test.txt"));

        File file = new File(".\\chatMessage_secureDatabase_test.txt");
        ChatMessage cm1 = new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo");
        ChatMessage cm2 = new ChatMessage(2, "Ana", new Timestamp(200000), "Hello");
        cm1.save(file);
        cm2.save(file);

        file = new File(".\\user_secureDatabase_test.txt");
        User user1 = new User("Jack", "JackMa", "666666");
        User user2 = new User("Jason", "JasonWu", "123456", 1);
        user1.save(file);
        user2.save(file);
    }

    @AfterEach
    void tearDown() {
        db = null;
        File file = new File(".\\chatMessage_secureDatabase_test.txt");
        file.delete();
        file = new File(".\\user_secureDatabase_test.txt");
        file.delete();
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
        User user1 = new User("Jack", "JackMa", "666666");
        User user2 = new User("Jason", "JasonWu", "123456", 1);
        Map<String, User> userMap = new HashMap<String, User>();
        userMap.put("Jack", user1);
        userMap.put("Jason", user2);
        assertEquals(userMap, db.readUsers());
    }

    @Test
    void addMessage() {
        ChatMessage cm = new ChatMessage(5, "Jack", new Timestamp(1000000), "Hello World");
        try {
            db.addMessage(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ChatMessage> msgList1 = db.readMessages();

        ChatMessage[] msgArray = new ChatMessage[] {
                new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello"),
                new ChatMessage(5, "Jack", new Timestamp(1000000), "Hello World")
        };
        List<ChatMessage> msgList2 = Arrays.asList(msgArray);
        assertEquals(msgList2, msgList1);
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
}