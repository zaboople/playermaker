package org.tmotte.common.util;

@FunctionalInterface
public interface ExceptionWrapper  {
    public void exec() throws Exception;
    public static void run(ExceptionWrapper ew) {
        try {
            ew.exec();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}