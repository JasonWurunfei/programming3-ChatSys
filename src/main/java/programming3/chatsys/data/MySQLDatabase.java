package programming3.chatsys.data;

import java.sql.*;

/**
 * Represents a MySQL database.
 * @author Wu Runfei (Jason SE181)
 */
public class MySQLDatabase extends SQLDatabase {

    public MySQLDatabase(
            String host, int port, String databaseName, String userName, String password) throws SQLException {

        this.connection = DriverManager.getConnection(
                "jdbc:mysql://"+host+":"+port+"/?serverTimezone=UTC", userName, password
        );
        String query = "CREATE DATABASE IF NOT EXISTS " + databaseName + ";";
        Statement statement = connection.createStatement();
        statement.execute(query);

        statement.execute("use " + databaseName + ";");

        this.createChatMessageTable();
        this.createUserTable();
        this.addDefaultUsers();
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
    @Override
    public void createUserTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS user (" +
                           "id integer PRIMARY KEY AUTO_INCREMENT," +
                           "username VARCHAR(255) NOT NULL UNIQUE," +
                           "fullname text NOT NULL," +
                           "password text NOT NULL," +
                           "last_read_id integer DEFAULT 0" +
                       ");";
        Statement statement = connection.createStatement();
        statement.execute(query);
    }

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
    @Override
    public void createChatMessageTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS chatmessage (" +
                           "id integer PRIMARY KEY AUTO_INCREMENT," +
                           "user integer NOT NULL," +
                           "time BIGINT NOT NULL," +
                           "message text NOT NULL" +
                       ");";
        Statement statement = connection.createStatement();
        statement.execute(query);
    }
}
