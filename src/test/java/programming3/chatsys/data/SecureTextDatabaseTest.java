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
        if (chatMessageDB.exists())
            chatMessageDB.delete();
        if (userDB.exists())
            userDB.delete();
    }

    @Test
    void readMessages() {
        List<ChatMessage> msgList = Arrays.asList(cm1, cm2);
        assertEquals(msgList, db.readMessages());
    }

    @Test
    void testReadMessagesWithGivenNum() {
        List<ChatMessage> msgList;

        msgList = Arrays.asList(cm2);
        assertEquals(msgList, db.readMessages(1));

        msgList = Arrays.asList(cm1, cm2);
        assertEquals(msgList, db.readMessages(2));

        // if request more than the total number of messages in db
        msgList = Arrays.asList(cm1, cm2);
        assertEquals(msgList, db.readMessages(1000));
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
        db.addMessage(new ChatMessage(5, "Jack", new Timestamp(1000000), "Hello World"));

        List<ChatMessage> msgList = Arrays.asList(
                new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello"),
                new ChatMessage(5, "Jack", new Timestamp(1000000), "Hello World")
        );

        assertEquals(msgList, db.readMessages());
    }

    @Test
    void lastId() {
        int lastId = 0;
        for (ChatMessage message : db.readMessages()) {
            if (message.getId() > lastId)
                lastId = message.getId();
        }
        assertEquals(lastId, db.lastId());
    }

    @Test
    void testAddMessage() {
        int lastId = db.lastId();
        ChatMessage cm = db.addMessage("Jack", "Hello World!!!");
        assertEquals(cm.getId(), lastId+1);

        List<ChatMessage> msgList = Arrays.asList(
                new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello"),
                new ChatMessage(cm.getId(), "Jack", cm.getTimestamp(), "Hello World!!!")
        );

        assertEquals(msgList, db.readMessages());
    }

    @Test
    void register() {
        User user = new User("Jane", "JaneDoe", "thisisapassword", 1);
        db.register(user);
        assertEquals(user, db.readUsers().get("Jane"));
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
        List<ChatMessage> msgList = Arrays.asList(
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello")
        );
        assertEquals(msgList, db.getUnreadMessages("Jason"));

        msgList = Arrays.asList(
                new ChatMessage(1, "Jack_1", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Ana", new Timestamp(200000), "Hello")
        );
        assertEquals(msgList, db.getUnreadMessages("Jack"));

        Map<String, User> userMap = db.readUsers();
        assertEquals(2, userMap.get("Jack").getLastReadId());
        assertEquals(2, userMap.get("Jason").getLastReadId());
    }

    @Test
    void authenticate() {
        assertTrue(db.authenticate("Jack", "666666"));
        assertTrue(db.authenticate("Jason", "123456"));
        assertFalse(db.authenticate("user1", "password"));
        assertFalse(db.authenticate("user1", "password"));
    }

    @Test
    void getUserIfAuthenticated() {
        assertEquals(user1, db.getUserIfAuthenticated("Jack", "666666"));
        assertNull(db.getUserIfAuthenticated("Jason", "666666"));
        assertNull(db.getUserIfAuthenticated("abc", "666666"));
    }

}