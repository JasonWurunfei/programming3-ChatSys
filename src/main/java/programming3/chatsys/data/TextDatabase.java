package programming3.chatsys.data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

public class TextDatabase implements Database {

    private File chatMessageDB = new File(".\\data\\messages.db");
    private File userDB = new File(".\\data\\user.db");

    public void setChatMessageDB(File chatMessageDB) {
        this.chatMessageDB = chatMessageDB;
    }

    public void setUserDB(File userDB) {
        this.userDB = userDB;
    }


    public TextDatabase() {
        if (!userDB.exists()) {
            new User("user1", "User1", "mypassword").save(userDB);
            new User("user_2", "Full Name", "Password").save(userDB);
        }
    }

    public List<ChatMessage> readMessages() {
        List<ChatMessage> msgList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(chatMessageDB), StandardCharsets.UTF_8))) {

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

    public List<ChatMessage> readMessages(int num) {
        List<ChatMessage> messages = this.readMessages();
        messages = messages.subList(
                Math.max(messages.size() - num, 0), messages.size());
        return messages;
    }

    public Map<String, User> readUsers() {

        Map<String, User> userMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(userDB), StandardCharsets.UTF_8))) {

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

    public void addMessage(ChatMessage msg) {
        if (this.lastId() >= msg.getId())
            msg.setId(this.lastId()+1);
        msg.save(chatMessageDB);
    }

    /**
     * Add a ChatMessage object to the database.
     * @param userName user who sends the message.
     * @param message the message to add.
     * @return ChatMessage object that just added.
     */
    public ChatMessage addMessage(String userName, String message) {
        ChatMessage chatMessage = new ChatMessage(
                this.lastId() + 1,
                userName,
                new Timestamp(new Date().getTime()),
                message
        );
        chatMessage.save(chatMessageDB);
        return chatMessage;
    }

    @Override
    public boolean register(User user) {
        Map<String, User> userMap = this.readUsers();
        if (userMap.get(user.getUserName()) == null) {
            user.save(this.userDB);
            return true;
        }
        return false;
    }

    @Override
    public int lastId() {
        int maxId = 0;
        for (ChatMessage chatMessage : this.readMessages()) {
            if (chatMessage.getId() > maxId) {
                maxId = chatMessage.getId();
            }
        }
        return maxId;
    }

    @Override
    public List<ChatMessage> getUnreadMessages(String userName) {
        User user = this.readUsers().get(userName);
        List<ChatMessage> unreadMessages = new LinkedList<>();
        List<ChatMessage> messages = this.readMessages();
        for(ChatMessage chatMessage : messages) {
            if (chatMessage.getId() > user.getLastReadId()) {
                unreadMessages.add(chatMessage);
            }
        }
        this.updateLastReadId(userName, this.lastId());
        return unreadMessages;
    }

    @Override
    public boolean authenticate(String userName, String password) {
        Map<String, User> userMap = this.readUsers();
        return userMap.get(userName) != null &&
                userMap.get(userName).getPassword().equals(password);
    }

    public User login(String userName, String password) {
        if (authenticate(userName, password)) {
            return this.readUsers().get(userName);
        } else {
            return null;
        }
    }

    private void updateLastReadId(String userName, int id) {
        Map<String, User> users = this.readUsers();
        users.get(userName).setLastReadId(id);
        userDB.delete();
        for (User user: users.values()) {
            user.save(userDB);
        }
    }
}
