package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadServer extends MessageQueue {
    private final int timeout = 500;
    private Database database;
    private Set<ThreadClient> clients;
    private final ReentrantLock lock = new ReentrantLock();

    ThreadServer(Database database) {
        this.database = database;
        this.clients = new HashSet<>();
    }

    public void initialize() {
        System.out.println("Server started.");
    }

    public void handleMessage(ChatMessage message) throws Exception {
        System.out.println("Server receiving message > " + message.getMessage());
        lock.lock();
        message.setId(this.database.lastId()+1);
        database.addMessage(message);
        lock.unlock();
        this.forward(message);
    }

    public void shutdown() {
        System.out.println("Server shutdown.");
    }

    private void forward(ChatMessage message) {
        for (ThreadClient client : this.clients) {
            client.send(message);
        }
    }

    public void register(ThreadClient client) {
        System.out.println("Client "+client.getName()+" registering");
        this.clients.add(client);
    }

    public void unregister(ThreadClient client) {
        System.out.println("Client "+client.getName()+" unregistering");
        this.clients.remove(client);
    }
}
