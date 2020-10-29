package programming3.chatsys.threads;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.TextDatabase;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class MessageQueueTest {

    MessageQueue queue;

    @BeforeEach
    void setUp() {
        queue = new ThreadServer(new TextDatabase());
    }

    @AfterEach
    void tearDown() {
        queue = null;
    }

    @Test
    void send() throws InterruptedException {
        ChatMessage m1 = new ChatMessage(1, "Jason", new Timestamp(10), "Hello");
        ChatMessage m2 = new ChatMessage(2, "Jack", new Timestamp(100), "Hei");
        BlockingQueue<ChatMessage> q = new LinkedBlockingQueue<>();

        queue.send(m1);
        queue.send(m2);
        q.offer(m1);
        q.offer(m2);

        Assertions.assertEquals(q.take(), queue.getQueue().take());
        Assertions.assertEquals(q.take(), queue.getQueue().take());
    }

    @Test
    void getMessage() throws InterruptedException {
        ChatMessage m = new ChatMessage(1, "Jason", new Timestamp(10), "Hello");
        queue.send(m);
        Assertions.assertEquals(m, queue.getMessage());
    }
}