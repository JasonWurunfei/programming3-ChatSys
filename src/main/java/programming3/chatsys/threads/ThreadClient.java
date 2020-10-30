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

    @Override
    public void initialize() {
        this.server.register(this);
        ChatMessage cm = new ChatMessage(0, "test", new Timestamp(0), "Hello World!");
        this.server.send(cm);
    }

    @Override
    public void shutdown() {
        this.server.unregister(this);
        System.out.println("Client " +this.getName()+" shutdown.");
    }

    @Override
    public void handleMessage(ChatMessage message) {
        System.out.println("Client "+this.name+" receiving message > "+ message.getMessage());
    }

}
