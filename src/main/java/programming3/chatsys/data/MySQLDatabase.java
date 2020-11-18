package programming3.chatsys.data;

import java.sql.*;

public class MySQLDatabase extends SQLDatabase {

    public MySQLDatabase(String host, int port, String databaseName, String userName, String password) throws SQLException {

        this.connection = DriverManager.getConnection(
                "jdbc:mysql://"+host+":"+port+"/?serverTimezone=UTC", userName, password);
        String query = "CREATE DATABASE IF NOT EXISTS " + databaseName + ";";
        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.execute("use "+databaseName+";");
        this.createChatMessageTable();
        this.createUserTable();

        // add default users
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
                "id integer PRIMARY KEY," +
                "user integer NOT NULL," +
                "time BIGINT NOT NULL," +
                "message text NOT NULL" +
                ");";
        Statement statement = connection.createStatement();
        statement.execute(query);
    }

    public static void main(String[] args) throws SQLException {
        Database db = new MySQLDatabase(
                "localhost", 3306, "test", "root", "123456");
        //db.register(new User("jason", "Jason Wu", "123456"));
        System.out.println(db.readUsers());
//        db.addMessage("jason", "Hello world!!");
//        db.addMessage("user1", "Hi!");
//        System.out.println(db.readUsers());
        //System.out.println(db.readMessages());
//        System.out.println(db.readUsers());
//        System.out.println(db.readMessages(3));
        ((MySQLDatabase) db).close();
    }
}
