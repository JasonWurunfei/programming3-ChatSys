package programming3.chatsys.cli;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.util.List;
import java.util.Scanner;

public class ReadMessages {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter number of messages >");
        int num = Integer.parseInt(input.nextLine());
        Database db = new Database(".\\database.txt");
        List<ChatMessage> messages = db.readMessages();
        if (num == 0) {
            for (ChatMessage cm : messages) {
                try {
                    System.out.println(cm.format());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (num > 0) {
            if (num > messages.size()) num = messages.size();
            for (int i = messages.size()-1; i >= messages.size()-num; i--) {
                try {
                    System.out.println(messages.get(i).format());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Can not read negative number of message.");
        }
    }
}
