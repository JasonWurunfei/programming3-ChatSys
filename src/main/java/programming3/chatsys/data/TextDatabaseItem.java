package programming3.chatsys.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * A TextDatabaseItem object can be formatted into string and save into text database.
 * Or it can parse a formatted string and set its attributes according to the data specified in the string.
 * @author Wu Runfei (Jason SE181)
 */
public abstract class TextDatabaseItem {
    protected TextDatabaseItem(String formatted) {
        this.parse(formatted);
    }
    protected TextDatabaseItem() {}

    /**
     * Save the formatted string of this TextDatabaseItem object into a text database.
     */
    public void save(File file) {
        try (BufferedWriter pw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            pw.write(this.format() + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Formats this TextDatabaseItem object into String representation.
     *
     * @return String representation of this TextDatabaseItem object.
     */
    abstract public String format();

    /**
     * Updates this TextDatabaseItem object with data from a formatted String.
     *
     * @param formatted A String representation of TextDatabaseItem object
     *                  like this: "<id>\t<userName>\t<timestamp>\t<message>"
     */
    abstract public void parse(String formatted);
}
