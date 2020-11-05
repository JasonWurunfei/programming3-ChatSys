package programming3.chatsys.cli;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;
import programming3.chatsys.data.TextDatabase;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class ReadUnreadMessages {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Database db = new TextDatabase(
                new File(".\\data\\messages.db"),
                new File(".\\data\\user.db")
        );
        System.out.print("Enter user name >");
        String username = input.nextLine();
        List<ChatMessage> messages = db.getUnreadMessages(username);
        for (ChatMessage cm : messages) {
            System.out.println(cm.format());
        }
    }
}
