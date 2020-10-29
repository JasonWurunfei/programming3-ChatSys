package programming3.chatsys.data;

import java.util.List;
import java.util.Map;

/**
 * @author Wu Runfei (Jason SE181)
 */
public interface Database {
    /**
     * Reads all the users data from the database file in and parse it
     * into a map of User Objects.
     *
     * @return Map<String, User> object containing all the user data,
     * in which the keys are usernames and the values are User objects
     */
    Map<String, User> readUsers();

    /**
     * Reads chat messages from the database file and parse it
     * into a list of ChatMessage Objects.
     *
     * @return List<ChatMessage> object containing all the ChatMessage objects
     */
    List<ChatMessage> readMessages();

    /**
     * get the first ChatMessage object of the message queue.
     *
     * @return the first ChatMessage object of the message queue.
     * @param msg ChatMessage object that is about to be add to the database
     * @throws Exception if the ChatMessage object given has smaller than
     * or equal to ID the biggest ID in the database.
     */
    void addMessage(ChatMessage msg) throws Exception;

    /**
     * Save a formatted User object into user database file
     * @param user User object that is about to be add to the database
     * @return true if user is successfully add to the database,
     * otherwise, false
     */
    boolean register(User user);

    /**
     * Get unread chat messages of some user from the database file.
     *
     * @param userName the name of the user
     * @return List<ChatMessage> object containing all the ChatMessage objects
     * which have bigger IDs than the user's last read ID.
     */
    List<ChatMessage> getUnreadMessages(String userName);

    /**
     * Check if the username and password are matching.
     * @param userName the name of the user
     * @param password the password of the user
     * @return the first ChatMessage object of the message queue.
     */
    boolean authenticate(String userName, String password);

    /**
     * get the biggest ID in the database file.
     * @return the last ID or 0 if the database is empty.
     */
    int lastId();
}
