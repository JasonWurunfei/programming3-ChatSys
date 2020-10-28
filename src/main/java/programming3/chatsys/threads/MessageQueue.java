package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;

import java.util.LinkedList;
import java.util.Queue;

public abstract class MessageQueue implements Runnable {
    private Queue<ChatMessage> queue;
    private int timeout = 500;

    public Queue<ChatMessage> getQueue() {
        return queue;
    }

    MessageQueue() {
        this.queue = new LinkedList<>();
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
     * @param waitTime The number of milliseconds that the thread will wait if
     *                 the queue is empty. after the waitTime it will try to read
     *                 again.
     * @return null if there is no message during the given waiting time. Otherwise,
     *              return the first ChatMessage object in its private message queue.
     * @throws InterruptedException when it is interrupted.
     */
    public ChatMessage getMessage(int waitTime) throws InterruptedException {
        if (this.queue.peek() == null) {
            Thread.sleep(waitTime);
        }
        return this.queue.poll();
    }


    public void run() {
        initialize();
        while (true) {
            try {
                ChatMessage message = this.getMessage(timeout);
                handleMessage(message);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        shutdown();
    }


    public abstract void initialize();
    public abstract void handleMessage(ChatMessage message) throws Exception;
    public abstract void shutdown();
}