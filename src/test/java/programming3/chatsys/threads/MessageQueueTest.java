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
    void send() {
        ChatMessage m1 = new ChatMessage(1, "Jason", new Timestamp(10), "Hello");
        ChatMessage m2 = new ChatMessage(2, "Jack", new Timestamp(100), "Hei");
        Queue<ChatMessage> q = new LinkedList<>();

        queue.send(m1);
        queue.send(m2);
        q.offer(m1);
        q.offer(m2);

        Assertions.assertEquals(q, queue.getQueue());
    }

    @Test
    void getMessage() throws InterruptedException {
        ChatMessage m = new ChatMessage(1, "Jason", new Timestamp(10), "Hello");
        queue.send(m);
        Assertions.assertEquals(m, queue.getMessage(10));
    }

    @Test
    void getMessageWait() throws InterruptedException {
        Assertions.assertEquals(null, queue.getMessage(2000));
    }
}