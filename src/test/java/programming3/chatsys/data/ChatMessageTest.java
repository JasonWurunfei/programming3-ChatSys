package programming3.chatsys.data;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class ChatMessageTest {
    ChatMessage cm;
    Timestamp time = new Timestamp(10000);
    File file = new File(".\\messages_test.db");

    @BeforeEach
    public void setUp() {
        cm = new ChatMessage(10, "Jason", time, "Hello World!");
    }
    @AfterEach
    public void tearDown() {
        cm = null;
    }

    @Test
    public void format() {
        assertEquals("10\t"+"Jason\t"+time+"\t"+"Hello World!", cm.format());
    }

    @Test
    public void parse() {
        cm.parse("100\tJack\t"+time+"\tHAHA");
        assertEquals(100, cm.getId());
        assertEquals("Jack", cm.getUserName());
        assertEquals(time, cm.getTimestamp());
        assertEquals("HAHA", cm.getMessage());
    }

    @Test
    public void parseWithNotEnoughTabulations() {
        assertThrows(IllegalArgumentException.class, () -> {
            cm.parse("Jack@\t"+time+"\tHAHA");
        },"The String does not contain enough tabulations and cannot be parsed");
    }


    @Test
    public void parseWithLineFeedInMessage() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ChatMessage("10\tJason\t"+time+"\tline1\nline2");
        }, "message contains a line feed");
    }

    @Test
    public void parseInvalidUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            cm.parse("100\tJack@\t"+time+"\tHAHA");
        }, "userName is invalid");
    }

    @Test
    public void parseInvalidIDs() {
        assertThrows(NumberFormatException.class, () -> {
            cm.parse("abc\tJack@\t"+time+"\tHAHA");
        });
        assertThrows(NumberFormatException.class, () -> {
            cm.parse("$%^&\tJack@\t"+time+"\tHAHA");
        });
    }

    @Test
    public void parseInvalidTimestamp() {
        assertThrows(IllegalArgumentException.class, () -> {
            cm.parse("abc\tJack@\t"+"2020/1/1"+"\tHAHA");
        });
    }

    @Test
    public void save() throws IOException {
        cm.save(file);

        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8));
        String line =  br.readLine();
        br.close();

        assertEquals("10\t"+"Jason\t"+time+"\t"+"Hello World!", line);
    }

    @AfterAll
    public static void clean() {
        File file = new File(".\\messages_test.db");
        if (file.exists())
            file.delete();
    }
}