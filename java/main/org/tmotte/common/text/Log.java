package org.tmotte.common.text;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

public class Log {
    private static boolean enabled=true;
    public static Set<String> types=new HashSet<>(
    );
    public static void add(String... toAdd) {
        for (String s: toAdd) types.add(s);
    }
    public static void remove(String... toAdd) {
        for (String s: toAdd) types.remove(s);
    }
    public static void with(Runnable r, String... toAdd) {
        add(toAdd);
        try {
            r.run();
        } finally {
            remove(toAdd);
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
