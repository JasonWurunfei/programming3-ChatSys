package programming3.chatsys.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SecureTextDatabase extends TextDatabase {
    final static ReadWriteLock MessageLOCK = new ReentrantReadWriteLock();
    final static ReadWriteLock UserLOCK = new ReentrantReadWriteLock();

    @Override
    public List<ChatMessage> readMessages() {
        MessageLOCK.readLock().lock();
        List<ChatMessage> messages = super.readMessages();
        MessageLOCK.readLock().unlock();
        return messages;
    }

    @Override
    public List<ChatMessage> readMessages(int num) {
        MessageLOCK.readLock().lock();
        List<ChatMessage> messages = super.readMessages(num);
        MessageLOCK.readLock().unlock();
        return messages;
    }

    @Override
    public Map<String, User> readUsers() {
        UserLOCK.readLock().lock();
        Map<String, User> users = super.readUsers();
        UserLOCK.readLock().unlock();
        return users;
    }

    @Override
    public void addMessage(ChatMessage msg) throws Exception {
        MessageLOCK.writeLock().lock();
        super.addMessage(msg);
        MessageLOCK.writeLock().unlock();
    }

    @Override
    public boolean register(User user) {
        UserLOCK.writeLock().lock();
        boolean res =  super.register(user);
        UserLOCK.writeLock().unlock();
        return res;
    }

    @Override
    public List<ChatMessage> getUnreadMessages(String userName) {
        UserLOCK.writeLock().lock();
        List<ChatMessage> messages = super.getUnreadMessages(userName);
        UserLOCK.writeLock().unlock();
        return messages;
    }
}
