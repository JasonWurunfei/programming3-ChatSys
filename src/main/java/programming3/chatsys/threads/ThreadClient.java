package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadClient extends MessageQueue {
    private ThreadServer server;
    private String name;
    private final static ReentrantLock LOCK = new ReentrantLock();

    ThreadClient(ThreadServer server, String name) {
        this.server = server;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void initialize() {
        this.server.register(this);
        ChatMessage cm = new ChatMessage(0, "test", new Timestamp(0), "Hello World!");
        LOCK.lock();
        this.server.send(cm);
        LOCK.unlock();
    }

    @Override
    public void shutdown() {
        System.out.println("Client shutdown.");
        this.server.unregister(this);
    }

    @Override
    public void handleMessage(ChatMessage message) {
        if (message != null) {
            System.out.println("Client "+this.name+" receiving message > "+ message.getMessage());
        }
    }

}