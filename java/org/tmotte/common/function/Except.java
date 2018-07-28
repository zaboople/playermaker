package org.tmotte.common.function;

public class Except {
    public static interface ExceptionalRunnable {
        public void run() throws Exception;
    }
    public static interface ExceptionalSupplier<T> {
        public T get() throws Exception;
    }
    public static <T> T get(ExceptionalSupplier<T> s) {
        try {
            return s.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void run(ExceptionalRunnable er) {
        try {
            er.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
