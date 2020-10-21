package programming3.chatsys.data;

import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Objects;

public class ChatMessage {
    private int id;
    private String userName;
    private Timestamp timestamp;
    private String message;

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }


    public ChatMessage(int id, String userName, Timestamp timestamp, String message) {
        this.id = id;
        this.userName = userName;
        this.timestamp = timestamp;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String format() {
        return this.id+"\t"+this.userName+"\t"+this.timestamp+"\t"+this.message+"\r\n";
    }

    public void parse(String formatted) {
        String[] data = formatted.split("\t");
        this.id = Integer.parseInt(data[0]);
        this.userName = data[1];
        this.timestamp = Timestamp.valueOf(data[2]);
        this.message = data[3];
    }

    public void save(File file) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));
            pw.write(this.format());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            assert pw != null;
            pw.close();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id == that.id &&
                userName.equals(that.userName) &&
                timestamp.equals(that.timestamp) &&
                message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, timestamp, message);
    }
}
