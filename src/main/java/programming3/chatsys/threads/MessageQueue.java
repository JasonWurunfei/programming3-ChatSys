package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Wu Runfei (Jason SE181)
 */
public abstract class MessageQueue implements Runnable {
    private BlockingQueue<ChatMessage> queue;
    private int timeout = 500;

    public BlockingQueue<ChatMessage> getQueue() {
        return queue;
    }

    MessageQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add a ChatMessage object to its private message queue.
     * @param message The ChatMessage object will be add to the queue.
     */
    public void send(ChatMessage message) {
        this.queue.offer(message);
    }

    /**
     * get the first ChatMessage object of the message queue.
     * @return the first ChatMessage object of the message queue.
     * @throws InterruptedException when it is interrupted.
     */
    public ChatMessage getMessage() throws InterruptedException {
        return this.queue.take();
    }

    public void run() {
        initialize();
        while (true) {
            try {
                ChatMessage message = this.getMessage();
                if (message != null) {
                    handleMessage(message);
                }
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        shutdown();
    }

    /**
     * Performs operations that needs to be done before the
     * MessageQueue starts to take in messages
     */
    public abstract void initialize();

    /**
     * Performs operations that needs to be done when receiving messages.
     * @param message received message
     */
    public abstract void handleMessage(ChatMessage message) throws Exception;

    /**
     * Performs operations that needs to be done after the
     * MessageQueue finishes its' execution.
     */
    public abstract void shutdown();
}
