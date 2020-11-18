package programming3.chatsys.data;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Represents a chat message.
 * @author Wu Runfei (Jason SE181)
 */
public class ChatMessage extends TextDatabaseItem {
    private int id;
    private String userName;
    private Timestamp timestamp;
    private String message;

    /**
     * Direct reused from "ChatSys - base" provided from Moodle
     *
     * Set value for the ChatMessage object
     * @param id unique id for this ChatMessage
     * @param userName name of the user who sent this ChatMessage
     * @param timestamp time at which this ChatMessage was sent
     * @param message message of this ChatMessage
     * @throws IllegalArgumentException If the username or message are not valid
     *         e.g. username contains alphanumerical characters and underscores or
     *         message contains line feed
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    private void init(int id, String userName, Timestamp timestamp, String message) {
        if (!User.userNameIsValid(userName)) {
            throw new IllegalArgumentException("userName is invalid");
        }
        if (message.indexOf('\n') >= 0) {
            throw new IllegalArgumentException("message contains a line feed");
        }
        this.id = id;
        this.userName = userName;
        this.timestamp = timestamp;
        this.message = message;
    }

    /**
     * Similar implementation as "ChatSys - base" provided from Moodle.
     * My old implementation at 6981aea (commit hash)
     *
     * Constructor of ChatMessage class used to create a new ChatMessage object.
     * The object is created with its attributes set according to the given params.
     *
     * @param id unique id for this ChatMessage
     * @param userName name of the owner of this ChatMessage
     * @param timestamp time at which this ChatMessage was sent
     * @param message message of this ChatMessage
     */
    public ChatMessage(int id, String userName, Timestamp timestamp, String message) {
        init(id, userName, timestamp, message);
    }

    /**
     * Constructor of ChatMessage class used to create a new ChatMessage object.
     * The object is created with its attributes set according to the given params.
     *
     * @param userName name of the owner of this ChatMessage
     * @param timestamp time at which this ChatMessage was sent
     * @param message message of this ChatMessage
     */
    public ChatMessage(String userName, Timestamp timestamp, String message) {
        init(-1, userName, timestamp, message);
    }

    /**
     * Similar implementation as "ChatSys - base" provided from Moodle.
     * My old implementation at 6981aea (commit hash)
     *
     * Constructor of ChatMessage class used to create a new ChatMessage object.
     * The object is created with its attributes set to the default value.
     */
    public ChatMessage() {
        init(-1, "_", null, "");
    }

    /**
     * Similar implementation as "ChatSys - base" provided from Moodle.
     * My old implementation at 6981aea (commit hash)
     *
     * Constructor of ChatMessage class used to create a new ChatMessage object.
     * The object is created with its message attribute set according to the given
     * message and the other attributes are set to default value.
     *
     * @param message message of this ChatMessage
     */
    public ChatMessage(String message) {
        init(-1, "_", null, message);
    }

    /**
     * Formats this ChatMessage object into String representation.
     *
     * @return String representation of this ChatMessage object.
     */
    public String format() {
        return this.id + "\t" + this.userName + "\t" + this.timestamp + "\t" + this.message;
    }

    /**
     * Similar implementation as "ChatSys - base" provided from Moodle.
     * My old implementation at 6981aea (commit hash)
     * Updates this ChatMessage object with data from a formatted String.
     *
     * @param formatted A String representation of ChatMessage object
     *                  like this: "<id>\t<userName>\t<timestamp>\t<message>"
     * @throws IllegalArgumentException If the String or username or timestamp are not formatted properly
     * @throws NumberFormatException If the id cannot be parsed properly
     */
    public void parse(String formatted) {
        String[] data = formatted.split("\t", 4);
        if (data.length == 4) {
            init(Integer.parseInt(data[0]), data[1], Timestamp.valueOf(data[2]), data[3]);
        } else {
            // reuse code from "ChatSys - base"
            throw new IllegalArgumentException(
                    "The String does not contain enough tabulations and cannot be parsed");
        }
    }


    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id == that.id &&
                userName.equals(that.userName) &&
                timestamp.equals(that.timestamp) &&
                message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, timestamp, message);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }

}
