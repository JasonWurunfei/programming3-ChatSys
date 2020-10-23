package programming3.chatsys.data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Database {
    private String filename;

    public Database(String filename) {
        this.filename = filename;
    }

    public List<ChatMessage> readMessages() {
        List<ChatMessage> msgList = new ArrayList<ChatMessage>();
        File file = new File(this.filename);
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
        File file = new File(this.filename);

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
        File file = new File(this.filename);

        // check ID
        List<ChatMessage> msgList = readMessages();
        int max_id = 0;
        for(int i = 0 ; i < msgList.size() ; i++) {
            if (msgList.get(i).getId() > max_id) {
                max_id = msgList.get(i).getId();
            }
        }
        if (msg.getId() <= max_id) {
            throw new Exception(
                    "ChatMessage's ID doesn't greater than" +
                            " all the ids of all the chat messages in datbase.");
        }
        msg.save(file);
    }
}
