package org.tmotte.common.text;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

/**
 * My hacky Logging thing. It's completely static. You are responsible for specifying a "type" when you
 * log, as the first parameter. Use add() to add types that will actually be logged; others are ignored.
 * <br>
 * This is reasonably thread-safe in that a given log() calls will not overlap with  thers; but adding &amp;
 * removing log types affects all threads.
 */
public class Log {
    private static boolean enabled=true;
    private static Set<String> types=new HashSet<>();

    public static void add(String... addTypes) {
        for (String s: addTypes) types.add(s);
    }
    public static void remove(String... removeTypes) {
        for (String s: removeTypes) types.remove(s);
    }

    public static void with(Runnable r, String... addTypes) {
        add(addTypes);
        try {
            r.run();
        } finally {
            remove(addTypes);
        }
    }

    public static void without(Runnable r, String... removeTypes) {
        remove(removeTypes);
        try {
            r.run();
        } finally {
            add(removeTypes);
        }
    }

    public static void log(String msgType, String message, Object... params) {
        if (!enabled || !types.contains(msgType))
            return;
        if (params.length > 0) {
            StringBuilder sb=new StringBuilder(message);
            int i=-1;
            for (Object p: params) {
                i=sb.indexOf("{}");
                if (i==-1) throw new RuntimeException("No {} found for putting: "+p);
                sb.replace(i, i+2, p==null ?"null" :p.toString());
            }
            message=sb.toString();
        }
        log(msgType, message);
    }

    public static void log(String msgType, String message) {
        if (!enabled || !types.contains(msgType))
            return;
        System.out.append(msgType).append(": ");
        System.out.println(message);
    }
}
