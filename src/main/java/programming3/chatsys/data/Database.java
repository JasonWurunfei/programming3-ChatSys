package programming3.chatsys.data;

import java.util.List;
import java.util.Map;

public interface Database {
    Map<String, User> readUsers();
    List<ChatMessage> readMessages();
    void addMessage(ChatMessage msg) throws Exception;
    boolean register(User user);
    List<ChatMessage> getUnreadMessages(String userName);
    boolean authenticate(String userName, String password);
    int lastId();
}
