package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Wu Runfei (Jason SE181)
 */
public class ThreadServer extends MessageQueue {
    private Database database;
    private Set<ThreadClient> clients;

    ThreadServer(Database database) {
        this.database = database;
        this.clients = Collections.synchronizedSet(new HashSet<>());
    }

    /**
     * Logs out server start message
     */
    @Override
    public void initialize() {
        System.out.println("Server started.");
    }

    /**
     * save the received messages into database.
     * @param message received message
     */
    @Override
    public void handleMessage(ChatMessage message) throws Exception {
        System.out.println("Server receiving message > " + message.getMessage());
        message.setId(this.database.lastId()+1);
        database.addMessage(message);
        this.forward(message);
    }

    /**
     * Logs out server shutdown message
     */
    @Override
    public void shutdown() {
        System.out.println("Server shutdown.");
    }

    /**
     * Sends message to every registered clients
     * @param message received message
     */
    private void forward(ChatMessage message) {
        for (ThreadClient client : this.clients) {
            client.send(message);
        }
    }

    /**
     * Puts the TreadClient object into clients set
     * @param client ThreadClient object that is about to be added
     */
    public void register(ThreadClient client) {
        System.out.println("Client "+client.getName()+" registering");
        this.clients.add(client);
    }

    /**
     * Removes the TreadClient object out of clients set
     * @param client ThreadClient object that is about to be removed
     */
    public void unregister(ThreadClient client) {
        System.out.println("Client "+client.getName()+" unregistering");
        this.clients.remove(client);
    }
}
