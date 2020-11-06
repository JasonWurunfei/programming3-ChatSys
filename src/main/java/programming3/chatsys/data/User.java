package programming3.chatsys.data;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a user.
 * @author Wu Runfei (Jason SE181)
 */
public class User extends TextDatabaseItem {

    private String userName;
    private String fullName;
    private String password;
    private int lastReadId;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^\\w+$");

    /**
     * Direct reused from "ChatSys - base" provided from Moodle
     *
     * Set value for the User object
     * @param userName the name of this user
     * @param fullName the full name of this user
     * @param password the password of this user
     * @param lastReadId the id of the last ChatMessage this user has read.
     * @throws IllegalArgumentException If the username or message are not valid
     *         e.g. username contains alphanumerical characters and underscores or
     *         fullName and password contain line feed
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    private void init(String userName, String fullName, String password, int lastReadId) {
        if (!userNameIsValid(userName)) {
            throw new IllegalArgumentException("userName is invalid");
        }
        if (fullName.indexOf('\n') >= 0) {
            throw new IllegalArgumentException("fullName contains a line feed");
        }
        if (password.indexOf('\n') >= 0) {
            throw new IllegalArgumentException("password contains a line feed");
        }
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.lastReadId = lastReadId;
    }

    /**
     * Similar implementation as "ChatSys - base" provided from Moodle.
     * My old implementation at 6981aea (commit hash)
     *
     * Constructor of User class used to create a new User object.
     * The object is created with its message attributes set according to the given params
     * and the lastReadId attribute is set to default value which is 0.
     *
     * @param userName the name of this user
     * @param fullName the full name of this user
     * @param password the password of this user
     * @throws IllegalArgumentException If the username or message are not valid
     *         e.g. username contains alphanumerical characters and underscores or
     *         fullName and password contain line feed
     */
    public User(String userName, String fullName, String password) {
        init(userName, fullName, password, 0);
    }

    /**
     * Similar implementation as "ChatSys - base" provided from Moodle.
     * My old implementation at 6981aea (commit hash)
     *
     * Constructor of User class used to create a new User object.
     * The object is created with its attributes set according to the given params.
     *
     * @param userName the name of this user
     * @param fullName the full name of this user
     * @param password the password of this user
     * @param lastReadId the id of the last ChatMessage this user has read.
     * @throws IllegalArgumentException If the username or message are not valid
     *         e.g. username contains alphanumerical characters and underscores or
     *         fullName and password contain line feed
     */
    public User(String userName, String fullName, String password, int lastReadId) {
        init(userName, fullName, password, lastReadId);
    }

    /**
     * Direct reused from "ChatSys - base" provided from Moodle
     * Creates a User from a formatted String.
     * @param formatted A User formatted like this: "<userName>\t<fullname>\t<password>\t<lastReadId>"
     * @throws IllegalArgumentException If the username or message are not valid
     *         e.g. username contains alphanumerical characters and underscores or
     *         fullName and password contain line feed
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    public User(String formatted) {
        super(formatted);
    }

    /**
     * Similar implementation as "ChatSys - base" provided from Moodle.
     *
     * Check whether a user name is formatted properly.
     * @param userName user name
     * @return true if the user name only contains alphanumerical characters and underscores.
     */
    public static boolean userNameIsValid(String userName) {
        return Pattern.matches(USERNAME_PATTERN.pattern(), userName);
    }

    /**
     * Formats this User object into String representation.
     *
     * @return String representation of this User object.
     */
    public String format() {
        return this.userName + "\t" + this.fullName+ "\t" + this.password + "\t" + this.lastReadId;
    }

    /**
     * Similar implementation as "ChatSys - base" provided from Moodle.
     * My old implementation at 6981aea (commit hash)
     * Updates this User object with data from a formatted String.
     *
     * @param formatted A String representation of User object
     *                  like this: "<userName>\t<fullname>\t<password>\t<lastReadId>"
     * @throws IllegalArgumentException If the String is not formatted properly
     * @throws NumberFormatException If the lastReadId cannot be parsed properly
     */
    public void parse(String formatted) throws IllegalArgumentException {
        String[] data = formatted.split("\t", 4);
        if (data.length == 4) {
            init(data[0], data[1], data[2], Integer.parseInt(data[3]));
        } else {
            // reuse code from "ChatSys - base"
            throw new IllegalArgumentException(
                    "The String does not contain enough tabulations and cannot be parsed");
        }
    }


    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public int getLastReadId() {
        return lastReadId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastReadId(int lastReadId) {
        this.lastReadId = lastReadId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return lastReadId == user.lastReadId &&
                userName.equals(user.userName) &&
                fullName.equals(user.fullName) &&
                password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, fullName, password, lastReadId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", lastReadId=" + lastReadId +
                '}';
    }
}
