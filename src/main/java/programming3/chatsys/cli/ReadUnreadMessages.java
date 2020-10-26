package programming3.chatsys.cli;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.util.List;
import java.util.Scanner;

public class ReadUnreadMessages {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Database db = new Database();
        System.out.print("Enter user name >");
        String username = input.nextLine();
        List<ChatMessage> messages = db.getUnreadMessages(username);
        for (ChatMessage cm : messages) {
            try {
                System.out.println(cm.format());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
