package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;

import java.sql.Timestamp;

/**
 * @author Wu Runfei (Jason SE181)
 */
public class ThreadClient extends MessageQueue {
    private ThreadServer server;
    private String name;

    ThreadClient(ThreadServer server, String name) {
        this.server = server;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Registers the client socket in the server and
     * send Hello world! to the server.
     */
    @Override
    public void initialize() {
        this.server.register(this);
        ChatMessage cm = new ChatMessage(0, "test", new Timestamp(0), "Hello World!");
        this.server.send(cm);
    }

    /**
     * Unregisters the client socket from the server.
     */
    @Override
    public void shutdown() {
        this.server.unregister(this);
        System.out.println("Client " +this.getName()+" shutdown.");
    }

    /**
     * Prints out the received message.
     * @param message received message
     */
    @Override
    public void handleMessage(ChatMessage message) {
        System.out.println("Client "+this.name+" receiving message > "+ message.getMessage());
    }

}
