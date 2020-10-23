package programming3.chatsys.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    public User(String userName, String fullName, String password) {
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
    }

    private String userName;
    private String fullName;
    private String password;
    private int lastReadId;

    public User() {
        this.lastReadId = 0;
    }

    public String format() throws Exception {
        if ((this.userName).contains("\n")) {
            throw new Exception("can not have '\\n' in the userName field.");
        }
        return this.userName+"\t"+this.fullName+"\t"+this.password+"\t"+this.lastReadId;
    }

    public void parse(String formatted) throws Exception {
        String[] data = formatted.split("\t");
        String userName = data[0];

        String pattern = "^\\w+$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(userName);
        if (matcher.find()) {
            this.userName = matcher.group(0);
        } else {
            throw new Exception("Username can only contain letters, numbers and the underscore");
        }

        this.fullName = data[1];
        this.password = data[2];
        this.lastReadId = Integer.parseInt(data[3]);
    }

    public void save(File file) {
        try(BufferedWriter pw = new BufferedWriter (new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            pw.write(this.format()+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public int getLastReadId() {
        return lastReadId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastReadId(int lastReadId) {
        this.lastReadId = lastReadId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return lastReadId == user.lastReadId &&
                userName.equals(user.userName) &&
                fullName.equals(user.fullName) &&
                password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, fullName, password, lastReadId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", lastReadId=" + lastReadId +
                '}';
    }
}
