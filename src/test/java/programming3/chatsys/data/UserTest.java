package programming3.chatsys.data;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    void format() {
        user.setUserName("Jason");
        user.setFullName("JasonWu");
        user.setPassword("123456");
        try {
            Assertions.assertEquals("Jason\tJasonWu\t123456\t0", user.format());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void parse() {
        String data = "Jack\tJackMa\t666666\t10";
        try {
            user.parse(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("Jack", user.getUserName());
        Assertions.assertEquals("JackMa", user.getFullName());
        Assertions.assertEquals("666666", user.getPassword());
        Assertions.assertEquals(10, user.getLastReadId());
    }

    @Test
    void save() {
        File file = new File(".\\user_test.txt");
        user.setUserName("Jason");
        user.setFullName("JasonWu");
        user.setPassword("123456");
        user.save(file);
        Assertions.assertEquals(true, file.exists());
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            Assertions.assertEquals("Jason\tJasonWu\t123456\t0", line);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clean() {
        File file = new File(".\\user_test.txt");
        if (file.exists())
            file.delete();
    }
}