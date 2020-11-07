package programming3.chatsys.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents all the protocols that the server supports
 * and provide distinguish different protocols functionality
 * @author Wu Runfei (Jason SE181)
 */
public class Protocol {

    private static final Map<String, Pattern> PATTERNS = new HashMap<>();
    static {
        PATTERNS.put("GET_RECENT", Pattern.compile("^GET recent messages (?<num>[\\d]+)$"));
        PATTERNS.put("GET_UNREAD", Pattern.compile("^GET unread messages$"));
        PATTERNS.put("POST_MESSAGE", Pattern.compile("^POST (?<message>.+[^\\s]+$)"));
        PATTERNS.put("LOGIN", Pattern.compile("^LOGIN (?<username>.+) (?<password>[^\\s]+)$"));
        PATTERNS.put("REGISTER", Pattern.compile(
                "^REGISTER (?<username>[^\\s]+) (?<fullName>[^\\s]+) (?<password>[^\\s]+)$"));
    }

    /**
     * Represents a matching result
     */
    public static class MatchTuple {
        public String type;
        public Matcher matcher;
        MatchTuple(String type, Matcher matcher) {
            this.type = type;
            this.matcher = matcher;
        }
    }

    /**
     * Finds a matching protocol for the given message.
     * @param message incoming message from client that need to be parsed
     * @return MatchTuple which specifies the protocol name and pattern matcher
     * or null if there is no matching protocol for that massage.
     */
    public static MatchTuple findMatch(String message) {
        for (Map.Entry<String, Pattern> entry : PATTERNS.entrySet()) {
            Matcher matcher = entry.getValue().matcher(message);
            if(matcher.find())
                return new MatchTuple(entry.getKey(), matcher);
        }
        return null;
    }
}
