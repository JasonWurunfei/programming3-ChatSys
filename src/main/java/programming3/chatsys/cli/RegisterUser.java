package programming3.chatsys.cli;

import programming3.chatsys.data.Database;
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
        Database db = new Database();
        Map<String, User> userMap = db.readUsers();
        if (userMap.get(username) != null) {
            System.out.println("This User is already registered.");
            return;
        } else {
            File file = new File(userDBPath);
            new User(username, fullName, password).save(file);
            System.out.println("Register success.");
        }
    }
}
