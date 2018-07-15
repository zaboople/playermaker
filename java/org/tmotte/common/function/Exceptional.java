package org.tmotte.common.function;

public interface Exceptional {
    public static interface ExceptionalRunnable {
        public void run() throws Exception;
    }
    public static interface ExceptionalSupplier<T> {
        public T get() throws Exception;
    }
    static <T> T get(ExceptionalSupplier<T> s) {
        try {
            return s.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    static void run(ExceptionalRunnable er) {
        try {
            er.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
