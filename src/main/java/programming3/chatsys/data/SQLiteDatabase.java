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

        // add default users
        if (this.readUsers().size() == 0) {
            this.register(new User("user1\tUser1\tmypassword\t0"));
            this.register(new User("user_2\tFull Name\tPassWord\t0"));
        }
    }

}
