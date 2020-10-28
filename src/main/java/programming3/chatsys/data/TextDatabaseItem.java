package programming3.chatsys.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public abstract class TextDatabaseItem {
    public void save(File file) {
        try (BufferedWriter pw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            pw.write(this.format() + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract public String format() throws Exception;
}
