package programming3.chatsys.data;

import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMessage extends TextDatabaseItem {
    private int id;
    private String userName;
    private Timestamp timestamp;
    private String message;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^\\w+$");

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
    public ChatMessage() {
        this.id = -1;
        this.userName = "";
        this.timestamp = null;
        this.message = "";
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

    public String format() throws Exception {
        if ((this.userName).contains("\n")) {
            throw new Exception("can not have '\\n' in the userName field.");
        }
        if ((this.message).contains("\n")) {
            throw new Exception("can not have '\\n' in the message field.");
        }
        return this.id+"\t"+this.userName+"\t"+this.timestamp+"\t"+this.message;
    }

    public void parse(String formatted) throws Exception {
        String[] data = formatted.split("\t");
        this.id = Integer.parseInt(data[0]);
        String userName = data[1];

        Matcher matcher = USERNAME_PATTERN.matcher(userName);
        if (matcher.find()) {
            this.userName = matcher.group(0);
        } else {
            throw new Exception("Username can only contain letters, numbers and the underscore");
        }

        this.timestamp = Timestamp.valueOf(data[2]);
        this.message = data[3];
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
