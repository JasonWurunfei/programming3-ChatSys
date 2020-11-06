package programming3.chatsys.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a text database with lock mechanism to secure concurrent execution.
 * @author Wu Runfei (Jason SE181)
 */
public class SecureTextDatabase extends TextDatabase {
    final static ReadWriteLock MESSAGE_LOCK = new ReentrantReadWriteLock();
    final static ReadWriteLock USER_LOCK = new ReentrantReadWriteLock();

    /**
     * Constructor of SecureTextDatabase class used to create a new SecureTextDatabase object.
     * if the user text database is not created yet, it will create a user database
     * with two default user records inside.
     * @param chatMessageDB
     * @param userDB
     */
    public SecureTextDatabase(File chatMessageDB, File userDB) {
        super(chatMessageDB, userDB);
    }

    /**
     * Reads chat messages from the database file and parse it
     * into a list of ChatMessage Objects in a thread-safe manner.
     *
     * @return List<ChatMessage> object contains all the ChatMessage
     * objects read from text database.
     * @throws FileNotFoundException if the message database file doesn't exists
     */
    @Override
    public List<ChatMessage> readMessages() {
        MESSAGE_LOCK.readLock().lock();
        try {
            return super.readMessages();
        } finally {
            MESSAGE_LOCK.readLock().unlock();
        }

    }

    /**
     * Reads `num` number of most recent ChatMessage objects from the database in a thread-safe manner.
     * @param num number of messages will be returned
     * @return List<ChatMessage> object containing `num` number of ChatMessage objects
     * if the given num parameter is bigger than the number of all the ChatMessages objects stored
     * in database, all the ChatMessages objects will be returned in a List<ChatMessage> object.
     * @throws FileNotFoundException if the message database file doesn't exists
     */
    @Override
    public List<ChatMessage> readMessages(int num) {
        MESSAGE_LOCK.readLock().lock();
        try {
            return super.readMessages(num);
        } finally {
            MESSAGE_LOCK.readLock().unlock();
        }
    }

    /**
     * Reads all the users data from the user database and parse it
     * into a map of User Objects in a thread-safe manner.
     *
     * @return Map<String, User> object containing all the user data,
     * in which the keys are usernames and the values are User objects
     * @throws FileNotFoundException if the user database file doesn't exists
     */
    @Override
    public Map<String, User> readUsers() {
        USER_LOCK.readLock().lock();
        try {
            return super.readUsers();
        } finally {
            USER_LOCK.readLock().unlock();
        }
    }

    /**
     * Saves a ChatMessage object to the database in a thread-safe manner.
     * @param message ChatMessage object that is about to be save into the database
     */
    @Override
    public void addMessage(ChatMessage message) {
        MESSAGE_LOCK.writeLock().lock();
        try {
            super.addMessage(message);
        } finally {
            MESSAGE_LOCK.writeLock().unlock();
        }
    }

    /**
     * Create a ChatMessage object and save it into the database in a thread-safe manner.
     * @param userName user who sends the message.
     * @param message the message of the new ChatMessage object.
     * @return ChatMessage object that just saved.
     */
    @Override
    public ChatMessage addMessage(String userName, String message) {
        MESSAGE_LOCK.writeLock().lock();
        try {
            return super.addMessage(userName, message);
        } finally {
            MESSAGE_LOCK.writeLock().unlock();
        }
    }

    /**
     * Save a formatted User object into the user database in a thread-safe manner.
     * @param user User object that is about to be add to the database
     * @return true if user is successfully add to the database,
     * otherwise, false
     */
    @Override
    public boolean register(User user) {
        USER_LOCK.writeLock().lock();
        try {
            return super.register(user);
        } finally {
            USER_LOCK.writeLock().unlock();
        }
    }

    /**
     * Get a User object in a thread-safe manner if matching userName and password is provided.
     * @param userName the name of the user
     * @param password the password of the user
     * @return corresponding User object if userName and password matches
     * otherwise null.
     */
    @Override
    public User getUserIfAuthenticated(String userName, String password) {
        USER_LOCK.readLock().lock();
        try {
            return super.getUserIfAuthenticated(userName, password);
        } finally {
            USER_LOCK.readLock().unlock();
        }
    }

    /**
     * Get unread chat messages of a user from the database file in a thread-safe manner.
     *
     * @param userName the name of the user
     * @return List<ChatMessage> object containing all the ChatMessage objects
     * which have bigger IDs than the user's last read ID.
     */
    @Override
    public List<ChatMessage> getUnreadMessages(String userName) {
        USER_LOCK.writeLock().lock();
        try {
            return super.getUnreadMessages(userName);
        } finally {
            USER_LOCK.writeLock().unlock();
        }
    }
}
