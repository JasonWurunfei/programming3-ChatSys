package programming3.chatsys.data;

import java.sql.*;
import java.util.*;
import java.util.Date;

public abstract class SQLDatabase implements Database {
    protected Connection connection;

    /**
     * Closes the SQLite database connection
     *
     * @throws SQLException if a database access error occurs.
     */
    public void close() throws SQLException {
        this.connection.close();
    }

    /**
     * add default users when database is created.
     */
    protected void addDefaultUsers() {
        if (this.readUsers().size() == 0) {
            this.register(new User("user1\tUser1\tmypassword\t0"));
            this.register(new User("user_2\tFull Name\tPassWord\t0"));
        }
    }

    /**
     * Create user table if it is not exist in the SQLite database
     *
     * @throws SQLException        if a database access error occurs or the methods is called
     *                             on a closed connection or statement or the execute method is called on a
     *                             PreparedStatement or CallableStatement.
     * @throws SQLTimeoutException when the driver has determined that the timeout value
     *                             that was specified by the setQueryTimeout method has been exceeded and has at
     *                             least attempted to cancel the currently running Statement
     */
    abstract public void createUserTable() throws SQLException;

    /**
     * Create chatmessage table if it is not exist in the SQLite database
     *
     * @throws SQLException        if a database access error occurs or the methods is called
     *                             on a closed connection or statement or the execute method is called on a
     *                             PreparedStatement or CallableStatement.
     * @throws SQLTimeoutException when the driver has determined that the timeout value
     *                             that was specified by the setQueryTimeout method has been exceeded and has at
     *                             least attempted to cancel the currently running Statement
     */
    abstract public void createChatMessageTable() throws SQLException;

    /**
     * Reads all the users data from the user table of the SQLite database and
     * parse it into a map of User Objects.
     *
     * @return Map<String, User> object containing all the user data,
     * in which the keys are usernames and the values are User objects
     */
    @Override
    public Map<String, User> readUsers() {
        String query = "SELECT * FROM user;";
        Map<String, User> userMap = new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                userMap.put(result.getString("username"), new User(
                        result.getString("username"),
                        result.getString("fullname"),
                        result.getString("password"),
                        result.getInt("last_read_id")
                ));
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return userMap;
    }

    /**
     * Reads chat messages from the SQLite database and parse it
     * into a list of ChatMessage Objects.
     *
     * @return List<ChatMessage> object contains all the ChatMessage
     * objects read from text database.
     */
    @Override
    public List<ChatMessage> readMessages() {
        List<ChatMessage> messages = new LinkedList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT chatmessage.*, user.username from chatmessage, user " +
                            "WHERE user.id = chatmessage.user;"
            );
            executeQueryChatMessages(statement, messages);
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return messages;
    }

    /**
     * Executes query that read ChatMessages from the database
     * and add the result to the output ChatMessage list
     */
    private void executeQueryChatMessages(
            PreparedStatement statement, List<ChatMessage> output) throws SQLException {
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            output.add(new ChatMessage(
                    result.getInt("id"),
                    result.getString("username"),
                    new Timestamp(result.getLong("time")),
                    result.getString("message")
            ));
        }
    }

    /**
     * Reads `num` number of most recent ChatMessage objects from the SQLite database.
     *
     * @param num number of messages will be returned
     * @return List<ChatMessage> object containing `num` number of ChatMessage objects
     * if the given num parameter is bigger than the number of all the ChatMessages objects stored
     * in database, all the ChatMessages objects will be returned in a List<ChatMessage> object.
     */
    @Override
    public List<ChatMessage> readMessages(int num) {
        List<ChatMessage> messages = new LinkedList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM (" +
                            "SELECT chatmessage.*, user.username from chatmessage, user " +
                            "WHERE user.id = chatmessage.user " +
                            "ORDER BY chatmessage.id DESC LIMIT ? " +
                            ") as res ORDER BY id ASC;"
            );
            statement.setInt(1, num);
            executeQueryChatMessages(statement, messages);
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return messages;
    }

    /**
     * Saves a ChatMessage object into the chatmessage table of the SQLite database.
     *
     * @param message ChatMessage object that is about to be save into the database
     */
    @Override
    public void addMessage(ChatMessage message) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO chatmessage(user, time, message) SELECT id, ?, ? FROM user WHERE username = ?;"
            );
            statement.setLong(1, message.getTimestamp().getTime());
            statement.setString(2, message.getMessage());
            statement.setString(3, message.getUserName());
            if(statement.executeUpdate() == 0)
                throw new SQLException("User " + message.getUserName() + " does not exist.");
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    /**
     * Create a ChatMessage object and save it into the chatmessage
     * table of the SQLite database.
     *
     * @param userName user who sends the message.
     * @param message  the message of the new ChatMessage object.
     * @return ChatMessage object that just saved.
     */
    @Override
    public ChatMessage addMessage(String userName, String message) {
        ChatMessage chatMessage = new ChatMessage(
                this.lastId() + 1, userName, new Timestamp(new Date().getTime()), message
        );
        this.addMessage(chatMessage);
        return chatMessage;
    }

    /**
     * Save a formatted User object into the user table of the SQLite database
     *
     * @param user User object that is about to be add to the database
     * @return true if user is successfully add to the database,
     * otherwise, false
     */
    @Override
    public boolean register(User user) {
        boolean success = false;
        try {
            String query = "INSERT INTO user(username, fullname, password, last_read_id) VALUES(?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getPassword());
            statement.setInt(4, user.getLastReadId());

            success = statement.executeUpdate() == 1;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return success;
    }

    /**
     * Get unread chat messages of a user from the SQLite database.
     *
     * @param userName the name of the user
     * @return List<ChatMessage> object containing all the ChatMessage objects
     * which have bigger IDs than the user's last read ID.
     */
    @Override
    public List<ChatMessage> getUnreadMessages(String userName) {
        List<ChatMessage> messages = new LinkedList<>();
        try {
            this.connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT chatmessage.id, u1.username, time, message FROM chatmessage, user u1, user u2 " +
                            "WHERE u1.id = user AND u2.username = ? AND u2.last_read_id < chatmessage.id;"
            );
            statement.setString(1, userName);
            this.executeQueryChatMessages(statement, messages);
            statement = connection.prepareStatement(
                    "UPDATE user SET last_read_id = ? WHERE username = ?"
            );
            if (messages.size() > 0) {
                statement.setInt(1, messages.stream().max(
                        Comparator.comparing(ChatMessage::getId)).get().getId());
                statement.setString(2, userName);
                statement.executeUpdate();
            }
            this.connection.commit();
            this.connection.setAutoCommit(true);
        } catch (SQLException error) {
            error.printStackTrace();
            try {
                this.connection.rollback();
                this.connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * Check if the username and password are matching.
     *
     * @param userName the name of the user
     * @param password the password of the user
     * @return the first ChatMessage object of the message queue.
     */
    @Override
    public boolean authenticate(String userName, String password) {
        boolean success = false;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM user WHERE username = ? AND password = ?;"
            );
            statement.setString(1, userName);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) == 1) success = true;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return success;
    }

    /**
     * Get the user object if the username and password are matching.
     *
     * @param userName the name of the user
     * @param password the password of the user
     * @return user object if the username and password are matching otherwise, null.
     */
    @Override
    public User getUserIfAuthenticated(String userName, String password) {
        User user = null;
        if (this.authenticate(userName, password)) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM user WHERE username = ? AND password = ?;"
                );
                statement.setString(1, userName);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("fullname"),
                        resultSet.getString("password"),
                        resultSet.getInt("last_read_id")
                );
            } catch (SQLException error) {
                error.printStackTrace();
            }
        }
        return user;
    }

    /**
     * get the biggest message ID in the message table of the SQLite database.
     *
     * @return the last ID or 0 if the database is empty.
     */
    @Override
    public int lastId() {
        int maxId = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT max(id) FROM chatmessage;");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            maxId = resultSet.getInt(1);
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return maxId;
    }

}
