package programming3.chatsys.data;

import java.sql.*;


/**
 * Represents a SQLite database.
 * @author Wu Runfei (Jason SE181)
 */
public class SQLiteDatabase extends SQLDatabase {

    public SQLiteDatabase(String DBPath) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + DBPath);
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
                "id integer PRIMARY KEY," +
                "username text NOT NULL UNIQUE," +
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
                "id integer PRIMARY KEY," +
                "user integer NOT NULL," +
                "time BIGINT NOT NULL," +
                "message text NOT NULL" +
                ");";
        Statement statement = connection.createStatement();
        statement.execute(query);
    }
}
