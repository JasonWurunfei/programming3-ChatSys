package programming3.chatsys.data;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SecureTextDatabase extends TextDatabase {
    final static ReadWriteLock MessageLOCK = new ReentrantReadWriteLock();
    final static ReadWriteLock UserLOCK = new ReentrantReadWriteLock();

    public SecureTextDatabase(File chatMessageDB, File userDB) {
        super(chatMessageDB, userDB);
    }

    @Override
    public List<ChatMessage> readMessages() {
        MessageLOCK.readLock().lock();
        try {
            return super.readMessages();
        } finally {
            MessageLOCK.readLock().unlock();
        }

    }

    @Override
    public List<ChatMessage> readMessages(int num) {
        MessageLOCK.readLock().lock();
        try {
            return super.readMessages(num);
        } finally {
            MessageLOCK.readLock().unlock();
        }
    }

    @Override
    public Map<String, User> readUsers() {
        UserLOCK.readLock().lock();
        try {
            return super.readUsers();
        } finally {
            UserLOCK.readLock().unlock();
        }
    }

    @Override
    public void addMessage(ChatMessage msg) {
        MessageLOCK.writeLock().lock();
        try {
            super.addMessage(msg);
        } finally {
            MessageLOCK.writeLock().unlock();
        }
    }

    @Override
    public ChatMessage addMessage(String userName, String message) {
        MessageLOCK.writeLock().lock();
        try {
            return super.addMessage(userName, message);
        } finally {
            MessageLOCK.writeLock().unlock();
        }
    }

    @Override
    public boolean register(User user) {
        UserLOCK.writeLock().lock();
        try {
            return super.register(user);
        } finally {
            UserLOCK.writeLock().unlock();
        }
    }

    @Override
    public User getUserIfAuthenticated(String userName, String password) {
        UserLOCK.readLock().lock();
        try {
            return super.getUserIfAuthenticated(userName, password);
        } finally {
            UserLOCK.readLock().unlock();
        }
    }

    @Override
    public List<ChatMessage> getUnreadMessages(String userName) {
        UserLOCK.writeLock().lock();
        try {
            return super.getUnreadMessages(userName);
        } finally {
            UserLOCK.writeLock().unlock();
        }
    }
}
