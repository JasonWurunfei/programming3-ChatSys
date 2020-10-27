package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.Database;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadServer extends MessageQueue implements Runnable {
    private int timeout = 500;
    private Database database;
    private Set<ThreadClient> clients;
    private ReentrantLock lock = new ReentrantLock();

    ThreadServer(Database database) {
        this.database = database;
        this.clients = new HashSet<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                ChatMessage message = this.getMessage(timeout);
                if (message != null) {
                    System.out.println("Server "+"receiving message > " + message.getMessage());
                    lock.lock();
                    int maxId = 0;
                    for (ChatMessage chatMessage : database.readMessages()) {
                        if (chatMessage.getId() > maxId) {
                            maxId = chatMessage.getId();
                        }
                    }
                    message.setId(maxId+1);
                    database.addMessage(message);
                    lock.unlock();
                    this.forward(message);
                }
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
