package programming3.chatsys.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an HTTP chat client
 * @author Wu Runfei (Jason SE181)
 */
public class HTTPChatClient {

    /**
     * Reused from Programming3.topic7.example1.SimpleHTTPClient
     * Sends a HTTP request and print the response
     * @param address URL address
     * @param method HTTP request method type
     * @param query query body
     * @author Maelick Claes (maelick.claes@oulu.fi)
     */
    public static void HTTPRequest(String address, String method, String query) throws IOException {
        System.out.println("Sending request to "+method+" "+address+"\nBody: "+query);
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if (query != null) {
            connection.setDoOutput(true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(query);
            writer.flush();
            writer.close();
        }
        System.out.println("Response: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        InputStream in;

        if (connection.getResponseCode() < 400) { in = connection.getInputStream(); }
        else { in = connection.getErrorStream(); }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            System.out.println(line);
        }

    }

    public static void main(String[] args) throws IOException {
        HTTPRequest("http://127.0.0.1:8080/recent/10", "GET", null);
        HTTPRequest("http://127.0.0.1:8080/unread/?username=jason&password=123456", "GET", null);

        HTTPRequest("http://127.0.0.1:8080/user/test_user", "POST",
                "{\"username\":\"test@user\",\"fullname\":\"test\n user\",\"password\":\"te\nst\"}"
        );
        HTTPRequest("http://127.0.0.1:8080/user/test_user", "POST",
                "{\"username\":\"test_user\",\"fullname\":\"test\\n user\",\"password\":\"test\"}"
        );
        HTTPRequest("http://127.0.0.1:8080/user/test_user", "POST",
                "{\"username\":\"testuser\",\"fullname\":\"test user\",\"password\":\"te\\nst\"}"
        );
        HTTPRequest("http://127.0.0.1:8080/message/?username=test@user&password=test", "POST",
                "{\"message\":\"Hello World!\"}"
        );
    }
}
