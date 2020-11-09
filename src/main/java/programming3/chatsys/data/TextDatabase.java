package programming3.chatsys.data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

/**
 * Represents a text database.
 * @author Wu Runfei (Jason SE181)
 */
public class TextDatabase implements Database {

    private File chatMessageDB;
    private File userDB;

    /**
     * Constructor of TextDatabase class used to create a new TextDatabase object.
     * if the user text database is not created yet, it will create a user database
     * with two default user records inside.
     * @param chatMessageDB
     * @param userDB
     */
    public TextDatabase(File chatMessageDB, File userDB) {
        this.chatMessageDB = chatMessageDB;
        this.userDB = userDB;
        if (!this.userDB.exists()) {
            new User("user1", "User1", "mypassword").save(this.userDB);
            new User("user_2", "Full Name", "PassWord").save(this.userDB);
        }
    }

    /**
     * Reads chat messages from the database file and parse it
     * into a list of ChatMessage Objects.
     *
     * @return List<ChatMessage> object contains all the ChatMessage
     * objects read from text database.
     * @throws FileNotFoundException if the message database file doesn't exists
     */
    public List<ChatMessage> readMessages() {
        List<ChatMessage> msgList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(chatMessageDB), StandardCharsets.UTF_8))) {
            String line;

            while((line = br.readLine()) != null) {
                ChatMessage msg = new ChatMessage();
                msg.parse(line);
                msgList.add(msg);
            }

        } catch(FileNotFoundException e) {
            return msgList;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return msgList;
    }

    /**
     * Reads `num` number of most recent ChatMessage objects from the database.
     * @param num number of messages will be returned
     * @return List<ChatMessage> object containing `num` number of ChatMessage objects
     * if the given num parameter is bigger than the number of all the ChatMessages objects stored
     * in database, all the ChatMessages objects will be returned in a List<ChatMessage> object.
     * @throws FileNotFoundException if the message database file doesn't exists
     */
    public List<ChatMessage> readMessages(int num) {
        List<ChatMessage> messages = this.readMessages();
        messages = messages.subList(
                Math.max(messages.size() - num, 0), messages.size());
        return messages;
    }

    /**
     * Reads all the users data from the user database and parse it
     * into a map of User Objects.
     *
     * @return Map<String, User> object containing all the user data,
     * in which the keys are usernames and the values are User objects
     * @throws FileNotFoundException if the user database file doesn't exists
     */
    public Map<String, User> readUsers() {
        Map<String, User> userMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(userDB), StandardCharsets.UTF_8))) {
            String line;

            while((line = br.readLine()) != null) {
                User user = new User(line);
                userMap.put(user.getUserName(), user);
            }

        } catch(FileNotFoundException e) {
            return userMap;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return userMap;
    }

    /**
     * Saves a ChatMessage object to the database.
     * @param message ChatMessage object that is about to be save into the database
     */
    public void addMessage(ChatMessage message) {
        if (this.lastId() >= message.getId())
            message.setId(this.lastId()+1);
        message.save(chatMessageDB);
    }

    /**
     * Create a ChatMessage object and save it into the database.
     * @param userName user who sends the message.
     * @param message the message of the new ChatMessage object.
     * @return ChatMessage object that just saved.
     */
    public ChatMessage addMessage(String userName, String message) {
        ChatMessage chatMessage = new ChatMessage(
                this.lastId() + 1,
                userName,
                new Timestamp(new Date().getTime()),
                message
        );
        chatMessage.save(chatMessageDB);
        return chatMessage;
    }

    /**
     * Save a formatted User object into the user database
     * @param user User object that is about to be add to the database
     * @return true if user is successfully add to the database,
     * otherwise, false
     */
    public boolean register(User user) {
        Map<String, User> userMap = this.readUsers();
        if (userMap.get(user.getUserName()) == null) {
            user.save(this.userDB);
            return true;
        }
        return false;
    }


    /**
     * get the biggest message ID in the message database file.
     * @return the last ID or 0 if the database is empty.
     */
    public int lastId() {
        int maxId = 0;
        for (ChatMessage chatMessage : this.readMessages()) {
            if (chatMessage.getId() > maxId) {
                maxId = chatMessage.getId();
            }
        }
        return maxId;
    }

    /**
     * Get unread chat messages of a user from the database file.
     *
     * @param userName the name of the user
     * @return List<ChatMessage> object containing all the ChatMessage objects
     * which have bigger IDs than the user's last read ID.
     */
    public List<ChatMessage> getUnreadMessages(String userName) {
        User user = this.readUsers().get(userName);
        List<ChatMessage> unreadMessages = new LinkedList<>();
        List<ChatMessage> messages = this.readMessages();
        for(ChatMessage chatMessage : messages) {
            if (chatMessage.getId() > user.getLastReadId()) {
                unreadMessages.add(chatMessage);
            }
        }
        this.updateLastReadId(userName, this.lastId());
        return unreadMessages;
    }

    /**
     * Check if the username and password are matching.
     * @param userName the name of the user
     * @param password the password of the user
     * @return the first ChatMessage object of the message queue.
     */
    public boolean authenticate(String userName, String password) {
        Map<String, User> userMap = this.readUsers();
        return userMap.get(userName) != null &&
                userMap.get(userName).getPassword().equals(password);
    }

    /**
     * Get a User object if matching userName and password is provided
     * @param userName the name of the user
     * @param password the password of the user
     * @return corresponding User object if userName and password matches
     * otherwise null.
     */
    public User getUserIfAuthenticated(String userName, String password) {
        if (authenticate(userName, password)) {
            return this.readUsers().get(userName);
        } else {
            return null;
        }
    }

    /**
     * Updates the user's lastReadId value in the database.
     * @param userName the name of the user
     * @param id new value for lastReadId attribute
     */
    private void updateLastReadId(String userName, int id) {
        Map<String, User> users = this.readUsers();
        users.get(userName).setLastReadId(id);
        userDB.delete();
        for (User user: users.values()) {
            user.save(userDB);
        }
    }
}
