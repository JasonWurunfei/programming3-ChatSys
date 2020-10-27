package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadClient extends MessageQueue implements Runnable {
    private int timeout = 500;
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
    public void run() {
        this.server.register(this);
        ChatMessage cm = new ChatMessage(0, "test", new Timestamp(0), "Hello World!");

        LOCK.lock();
        this.server.send(cm);
        LOCK.unlock();

        while (true) {
            try {
                ChatMessage message = this.getMessage(timeout);
                if (message != null) {
                    System.out.println("Client "+this.name+" receiving message > "+message.getMessage());
                }
            } catch (InterruptedException e) {
                this.server.unregister(this);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
