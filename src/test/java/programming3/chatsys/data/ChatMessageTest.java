package programming3.chatsys.data;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class ChatMessageTest {
    ChatMessage cm;

    @BeforeEach
    public void setUp() {
        cm = new ChatMessage(0, "", new Timestamp(0), "");
    }
    @AfterEach
    public void tearDown() {
        cm = null;
    }

    @Test
    public void format() {
        cm.setId(10);
        Timestamp time = new Timestamp(10000);
        cm.setTimestamp(time);
        cm.setUserName("Jason");
        cm.setMessage("Hello World!");
        Assertions.assertEquals("10\t"+"Jason\t"+time+"\t"+"Hello World!\r\n", cm.format());
    }

    @Test
    public void parse() {
        Timestamp time = new Timestamp(10000);
        cm.parse("100\tJack\t"+time+"\tHAHA");
        ChatMessage cm2 = new ChatMessage(100, "Jack", time, "HAHA");
        Assertions.assertEquals(cm2, cm);
    }

    @Test
    public void save() {
        File file = new File(".\\messages_test.txt");
        cm.setId(10);
        Timestamp time = new Timestamp(10000);
        cm.setTimestamp(time);
        cm.setUserName("Jason");
        cm.setMessage("Hello World!");
        cm.save(file);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line =  br.readLine();
            Assertions.assertEquals("10\t"+"Jason\t"+time+"\t"+"Hello World!", line);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clean() {
        File file = new File(".\\messages_test.txt");
        file.delete();
    }
}