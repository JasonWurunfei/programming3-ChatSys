package programming3.chatsys.cli;

import programming3.chatsys.data.Database;
import programming3.chatsys.data.TextDatabase;
import programming3.chatsys.data.User;

import java.io.File;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUser {
    public static void main(String[] args) {
        String userDBPath = "./UserDatabase.txt";
        Scanner input = new Scanner(System.in);

        // Ask for user name.
        String pattern = "^[\\w]+$";
        Pattern regex = Pattern.compile(pattern);
        System.out.print("Enter username >");
        String username = input.nextLine();
        Matcher matcher = regex.matcher(username);
        while (!matcher.find()) {
            System.out.print("Username can only contain letters, numbers and the underscore\n");
            System.out.print("Enter username >");
            username = input.nextLine();
            matcher = regex.matcher(username);
        }

        System.out.print("Enter full name >");
        String fullName = input.nextLine();
        System.out.print("Enter password >");
        String password = input.nextLine();
        Database db = new TextDatabase();
        User user = new User(username, fullName, password);
        if (db.register(user)) {
            System.out.println("Register success.");
        } else {
            System.out.println("This User is already registered.");
        }
    }
}
