package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ThreadServer extends MessageQueue {
    private Database database;
    private Set<ThreadClient> clients;

    ThreadServer(Database database) {
        this.database = database;
        this.clients = Collections.synchronizedSet(new HashSet<>());
    }

    public void initialize() {
        System.out.println("Server started.");
    }

    public void handleMessage(ChatMessage message) throws Exception {
        System.out.println("Server receiving message > " + message.getMessage());
        message.setId(this.database.lastId()+1);
        database.addMessage(message);
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
