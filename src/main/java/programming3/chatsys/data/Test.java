package programming3.chatsys.data;
import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) {
        File file = new File(".\\messages_test.txt");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line =  br.readLine();
            System.out.println(line);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
