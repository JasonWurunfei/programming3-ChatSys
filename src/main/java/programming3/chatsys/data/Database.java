package programming3.chatsys.data;

import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
                msg.parse(line);
                msgList.add(msg);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return msgList;
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
