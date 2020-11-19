package programming3.chatsys.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MySQLDatabaseTest {
    MySQLDatabase db;
    final String MYSQL_HOST = "localhost";
    final String MYSQL_USERNAME = "root";
    final String MYSQL_PASSWORD = "123456";
    final String MYSQL_DB_NAME = "testDB";
    final int MYSQL_PORT = 3306;

    Connection connection;

    ChatMessage cm1 = new ChatMessage(1, "Jack", new Timestamp(100000), "Haloo");
    ChatMessage cm2 = new ChatMessage(2, "Jason", new Timestamp(200000), "Hello");

    User user1 = new User("Jack", "JackMa", "666666");
    User user2 = new User("Jason", "JasonWu", "123456", 1);
    User defaultUser1 = new User("user1\tUser1\tmypassword\t0");
    User defaultUser2 = new User("user_2\tFull Name\tPassWord\t0");

    private void createTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("use " + MYSQL_DB_NAME + ";");
        String query = "CREATE TABLE IF NOT EXISTS user (" +
                "id integer PRIMARY KEY AUTO_INCREMENT," +
                "username VARCHAR(255) NOT NULL UNIQUE," +
                "fullname text NOT NULL," +
                "password text NOT NULL," +
                "last_read_id integer DEFAULT 0" +
                ");";
        statement = connection.createStatement();
        statement.execute(query);
        query = "CREATE TABLE IF NOT EXISTS chatmessage (" +
                "id integer PRIMARY KEY AUTO_INCREMENT," +
                "user integer NOT NULL," +
                "time BIGINT NOT NULL," +
                "message text NOT NULL" +
                ");";
        statement = connection.createStatement();
        statement.execute(query);
    }

    private void loadTestData(Connection connection) throws SQLException {
        // add user1 to the database
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO user(username, fullname, password) VALUES(?, ?, ?);"
        );
        statement.setString(1, user1.getUserName());
        statement.setString(2, user1.getFullName());
        statement.setString(3, user1.getPassword());
        statement.executeUpdate();

        // add user2 to the database
        statement = connection.prepareStatement(
                "INSERT INTO user(username, fullname, password, last_read_id) VALUES(?, ?, ?, ?);"
        );
        statement.setString(1, user2.getUserName());
        statement.setString(2, user2.getFullName());
        statement.setString(3, user2.getPassword());
        statement.setInt(4, user2.getLastReadId());
        statement.executeUpdate();

        // add cm1 to the database
        statement = connection.prepareStatement(
                "INSERT INTO chatmessage(user, time, message) SELECT id, ?, ? FROM user WHERE username = ?;"
        );
        statement.setLong(1, cm1.getTimestamp().getTime());
        statement.setString(2, cm1.getMessage());
        statement.setString(3, cm1.getUserName());
        statement.executeUpdate();

        // add cm2 to the database
        statement = connection.prepareStatement(
                "INSERT INTO chatmessage(user, time, message) SELECT id, ?, ? FROM user WHERE username = ?;"
        );
        statement.setLong(1, cm2.getTimestamp().getTime());
        statement.setString(2, cm2.getMessage());
        statement.setString(3, cm2.getUserName());
        statement.executeUpdate();
    }

    @BeforeEach
    void setUp() throws SQLException {
        db = new MySQLDatabase(
                MYSQL_HOST,
                MYSQL_PORT,
                MYSQL_DB_NAME,
                MYSQL_USERNAME,
                MYSQL_PASSWORD
        );
        connection = DriverManager.getConnection(
                "jdbc:mysql://"+MYSQL_HOST+":"+MYSQL_PORT+"/?serverTimezone=UTC",
                MYSQL_USERNAME, MYSQL_PASSWORD
        );
        this.createTable(connection);
        this.loadTestData(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        String query = "DROP DATABASE "+ MYSQL_DB_NAME + ";";
        Statement statement = connection.createStatement();
        statement.execute(query);
        db.close();
        connection.close();
        connection = null;
        db = null;
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
        db.addMessage(new ChatMessage(5, "Jack", new Timestamp(1000000L), "Hello World"));

        List<ChatMessage> msgList = Arrays.asList(
                new ChatMessage(1, "Jack", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Jason", new Timestamp(200000), "Hello"),
                new ChatMessage(3, "Jack", new Timestamp(1000000L), "Hello World")
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
                new ChatMessage(1, "Jack", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Jason", new Timestamp(200000), "Hello"),
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
                new ChatMessage(2, "Jason", new Timestamp(200000), "Hello")
        );
        assertEquals(msgList, db.getUnreadMessages("Jason"));

        msgList = Arrays.asList(
                new ChatMessage(1, "Jack", new Timestamp(100000), "Haloo"),
                new ChatMessage(2, "Jason", new Timestamp(200000), "Hello")
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