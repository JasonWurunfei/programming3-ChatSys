package programming3.chatsys.data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Database {

    private String chatMessageDBPath = ".\\chatMessageDatabase.txt";
    private String userDBPath = ".\\userDatabase.txt";

    public String getChatMessageDBPath() {
        return chatMessageDBPath;
    }

    public void setChatMessageDBPath(String chatMessageDBPath) {
        this.chatMessageDBPath = chatMessageDBPath;
    }

    public String getUserDBPath() {
        return userDBPath;
    }

    public void setUserDBPath(String userDBPath) {
        this.userDBPath = userDBPath;
    }


    public Database(String chatMessageDBPath, String userDBPath) {
        this.userDBPath = userDBPath;
        this.chatMessageDBPath = chatMessageDBPath;
    }

    public Database() {}

    public List<ChatMessage> readMessages() {
        List<ChatMessage> msgList = new ArrayList<ChatMessage>();
        File file = new File(this.chatMessageDBPath);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while((line = br.readLine()) != null) {
                ChatMessage msg = new ChatMessage();
                try {
                    msg.parse(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msgList.add(msg);
            }
        } catch(FileNotFoundException e) {
            return msgList;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return msgList;
    }

    public Map<String, User> readUsers() {

        Map<String, User> userMap = new HashMap<String, User>();
        File file = new File(this.userDBPath);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while((line = br.readLine()) != null) {
                User user = new User();
                try {
                    user.parse(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userMap.put(user.getUserName(), user);
            }
        } catch(FileNotFoundException e) {
            return userMap;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return userMap;
    }

    public void addMessage(ChatMessage msg) throws Exception {
        File file = new File(this.chatMessageDBPath);

        // check ID
        List<ChatMessage> msgList = this.readMessages();
        int maxId = this.getMaxId(msgList);
        if (msg.getId() <= maxId) {
            throw new Exception(
                    "ChatMessage's ID doesn't greater than" +
                            " all the ids of all the chat messages in datbase.");
        }
        msg.save(file);
    }

    private int getMaxId(List<ChatMessage> list) {
        int maxId = 0;
        for (ChatMessage chatMessage : list) {
            if (chatMessage.getId() > maxId) {
                maxId = chatMessage.getId();
            }
        }
        return maxId;
    }

    public List<ChatMessage> getUnreadMessages(String userName) {
        User user = this.readUsers().get(userName);
        List<ChatMessage> unreadMessages = new LinkedList<>();
        List<ChatMessage> messages = this.readMessages();
        for(ChatMessage chatMessage : messages) {
            if (chatMessage.getId() > user.getLastReadId()) {
                unreadMessages.add(chatMessage);
            }
        }
        this.updateLastReadId(userName, this.getMaxId(messages));
        return unreadMessages;
    }

    private void updateLastReadId(String userName, int id) {
        Map<String, User> users = this.readUsers();
        users.get(userName).setLastReadId(id);
        File file = new File(this.userDBPath);
        file.delete();
        for (User user: users.values()) {
            user.save(file);
        }
    }

}
