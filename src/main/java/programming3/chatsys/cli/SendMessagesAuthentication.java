package programming3.chatsys.cli;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;
import programming3.chatsys.data.TextDatabase;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendMessagesAuthentication {
    public static void main(String[] args) {

        // Ask for user name.
        String pattern = "^[\\w]+$";
        Pattern regex = Pattern.compile(pattern);
        Scanner input = new Scanner(System.in);

        System.out.print("Enter your user name >");
        String username = input.nextLine();
        Matcher matcher = regex.matcher(username);
        while (!matcher.find()) {
            System.out.print("Username can only contain letters, numbers and the underscore\n");
            System.out.print("Enter your user name >");
            username = input.nextLine();
            matcher = regex.matcher(username);
        }

        System.out.print("Enter your password >");
        String password = input.nextLine();

        // Authentication
        Database db = new TextDatabase(
                new File(".\\data\\messages.db"),
                new File(".\\data\\user.db")
        );
        if (!db.authenticate(username, password)) {
            System.out.println("Wrong username or password.");
            return;
        }

        // set last id
        String chatMsgDBPath = ".\\chatMessageDatabase.txt";
        File file = new File(chatMsgDBPath);
        int last_id = 0;
        if(file.exists() && file.length() != 0) {
            // get the biggest ChatMessage ID
            for (ChatMessage cm : db.readMessages()) {
                if (last_id < cm.getId()) {
                    last_id = cm.getId();
                }
            }
        }

        // receive user input messages
        System.out.print("Enter your message >");
        String msg = input.nextLine();
        ChatMessage cm = new ChatMessage(last_id+1, username, new Timestamp(new Date().getTime()), msg);

        // save in the database
        cm.save(file);
    }
}
