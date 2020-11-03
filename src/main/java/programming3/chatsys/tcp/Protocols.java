package programming3.chatsys.tcp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

public class Protocols {
    public static final Map<String, Pattern> PATTERNS = new HashMap();
    static {
        PATTERNS.put("GET_RECENT", Pattern.compile("^GET recent messages (?<num>[\\d]+)$"));
        PATTERNS.put("GET_UNREAD", Pattern.compile("^GET unread messages$"));
        PATTERNS.put("POST_MESSAGE", Pattern.compile("^POST (?<message>.+)$"));
        PATTERNS.put("LOGIN", Pattern.compile("^LOGIN (?<username>.+) (?<password>.+)$"));
        PATTERNS.put("REGISTER", Pattern.compile("^REGISTER (?<username>.+) (?<fullName>.+) (?<password>.+)$"));
    }
}
