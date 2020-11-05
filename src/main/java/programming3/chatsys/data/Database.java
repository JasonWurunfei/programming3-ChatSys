package programming3.chatsys.data;

import java.util.List;
import java.util.Map;

/**
 * @author Wu Runfei (Jason SE181)
 */
public interface Database {
    /**
     * Reads all the users data from the user database and parse it
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
     * @return List<ChatMessage> object contains all the ChatMessage
     * objects read from text database.
     */
    List<ChatMessage> readMessages();

    /**
     * Reads `num` number of most recent ChatMessage objects from the database.
     * @param num number of messages will be returned
     * @return List<ChatMessage> object containing `num` number of ChatMessage objects
     * if the given num parameter is bigger than the number of all the ChatMessages objects stored
     * in database, all the ChatMessages objects will be returned in a List<ChatMessage> object.
     */
    List<ChatMessage> readMessages(int num);

    /**
     * Saves a ChatMessage object to the database.
     * @param message ChatMessage object that is about to be save into the database
     */
    void addMessage(ChatMessage message);

    /**
     * Create a ChatMessage object and save it into the database.
     * @param userName user who sends the message.
     * @param message the message of the new ChatMessage object.
     * @return ChatMessage object that just saved.
     */
    ChatMessage addMessage(String userName, String message);

    /**
     * Save a formatted User object into the user database
     * @param user User object that is about to be add to the database
     * @return true if user is successfully add to the database,
     * otherwise, false
     */
    boolean register(User user);

    /**
     * Get unread chat messages of a user from the database file.
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

    User getUserIfAuthenticated(String userName, String password);

    /**
     * get the biggest message ID in the message database file.
     * @return the last ID or 0 if the database is empty.
     */
    int lastId();
}
