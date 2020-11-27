package programming3.chatsys.http;

import programming3.chatsys.data.MySQLDatabase;
import programming3.chatsys.data.SQLiteDatabase;
import programming3.chatsys.data.SecureTextDatabase;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class RunServer {
    public static boolean supportsMySQL = true;
    public static boolean supportsTextDB = true;
    public static boolean supportsSQLite = true;

    // file database run method
    public static void run(int port, String messagesDb, String usersDb) throws IOException {
        new HTTPChatServer(new SecureTextDatabase(new File(messagesDb), new File(usersDb)), port).start();
    }

    // SQLite database run method
    public static void run(int port, String database) throws SQLException, IOException {
        new HTTPChatServer(new SQLiteDatabase(database), port).start();
    }

    // MySQL database run method
    public static void run(int port, String dbHost, int dbPort, String dbUser, String dbPassword, String dbName) throws SQLException, IOException {
        new HTTPChatServer(new MySQLDatabase(dbHost, dbPort, dbName, dbUser, dbPassword), port).start();
    }

    public static void main(String[] args) throws IOException, SQLException {
        // run with text database
        // run(8080, "testMessage.db", "testUser.db");

        // run with SQLite database
         run(8080, "data/testDB.sqlite");

        // run with MySQL database
        //run(8080, "localhost", 3306, "root", "123456", "chatsys");
    }
}
