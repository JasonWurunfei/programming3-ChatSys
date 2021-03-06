package programming3.chatsys.threads;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import programming3.chatsys.data.ChatMessage;
import programming3.chatsys.data.TextDatabase;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class MessageQueueTest {

    MessageQueue queue;
    File userDB = new File(".\\user_test.db");

    @BeforeEach
    void setUp() {
        queue = new ThreadServer(new TextDatabase(
                    new File(".\\message_test.db"),
                    new File(".\\user_test.db")
        ));
        if (userDB.exists())
            userDB.delete();
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

        assertEquals(q.take(), queue.getQueue().take());
        assertEquals(q.take(), queue.getQueue().take());
    }

    @Test
    void getMessage() throws InterruptedException {
        ChatMessage m = new ChatMessage(1, "Jason", new Timestamp(10), "Hello");
        queue.send(m);
        assertEquals(m, queue.getMessage());
    }
}