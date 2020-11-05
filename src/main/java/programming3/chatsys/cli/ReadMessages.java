package programming3.chatsys.cli;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;
import programming3.chatsys.data.TextDatabase;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class ReadMessages {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter number of messages >");
        int num = Integer.parseInt(input.nextLine());
        Database db = new TextDatabase(
                new File(".\\data\\messages.db"),
                new File(".\\data\\user.db")
        );
        List<ChatMessage> messages = db.readMessages();
        if (num == 0) {
            for (ChatMessage cm : messages) {
                System.out.println(cm.format());
            }
        } else if (num > 0) {
            if (num > messages.size()) num = messages.size();
            for (int i = messages.size()-1; i >= messages.size()-num; i--) {
                System.out.println(messages.get(i).format());
            }
        } else {
            System.out.println("Can not read negative number of message.");
        }
    }
}
