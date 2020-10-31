package programming3.chatsys.tcp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

public class Protocols {
    public static final Map<String, Pattern> PATTERNS = new HashMap();
    static {
        PATTERNS.put("GET_RECENT", Pattern.compile("^GET recent messages ([\\d]+)$"));
    }
}
