package programming3.chatsys.data;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    User user;
    File file = new File(".\\user_test.db");

    @BeforeEach
    void setUp() {
        user = new User("Jason\tJasonWu\t123456\t0");
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    void format() {
        assertEquals("Jason\tJasonWu\t123456\t0", user.format());
    }

    @Test
    void parse() {
        user.parse("Jack\tJackMa\t666666\t10");
        assertEquals("Jack", user.getUserName());
        assertEquals("JackMa", user.getFullName());
        assertEquals("666666", user.getPassword());
        assertEquals(10, user.getLastReadId());
    }

    @Test
    public void parseWithNotEnoughTabulations() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.parse("Jack\t666666\t10");
        },"The String does not contain enough tabulations and cannot be parsed");
    }


    @Test
    public void parseWithLineFeed() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("Jason\tJason\nWu\t123456\t0");
        }, "fullName contains a line feed");
        assertThrows(IllegalArgumentException.class, () -> {
            new User("Jason\tJasonWu\t12\n3456\t0");
        }, "password contains a line feed");
    }

    @Test
    public void parseInvalidUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.parse("Jas\non\tJasonWu\t123456\t0");
        }, "userName is invalid");
        assertThrows(IllegalArgumentException.class, () -> {
            user.parse("Jason@\tJasonWu\t123456\t0");
        }, "userName is invalid");
        assertThrows(IllegalArgumentException.class, () -> {
            user.parse("Jason wu\tJasonWu\t123456\t0");
        }, "userName is invalid");
        assertThrows(IllegalArgumentException.class, () -> {
            user.parse("吴润飞\tJasonWu\t123456\t0");
        }, "userName is invalid");
    }

    @Test
    public void parseInvalidLastReadIds() {
        assertThrows(NumberFormatException.class, () -> {
            user.parse("Jason\tJasonWu\t123456\tabc");
        });
        assertThrows(NumberFormatException.class, () -> {
            user.parse("Jason\tJasonWu\t123456\t12@#@");
        });
    }

    @Test
    void save() throws IOException {
        user.save(file);
        assertEquals(true, file.exists());
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8));
        String line = br.readLine();
        br.close();

        assertEquals("Jason\tJasonWu\t123456\t0", line);
    }

    @AfterAll
    public static void clean() {
        File file = new File(".\\user_test.db");
        if (file.exists())
            file.delete();
    }
}