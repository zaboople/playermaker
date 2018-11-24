package org.tmotte.common.function;

/**
 * This is a lambdafication tool for dealing with the typical java.util.function functional interfaces
 * and their refusal to allow checked exceptions. Here, we handle checked exceptions by wrapping them
 * in RuntimeExceptions, which means things will still blow up, but there's no need for special handling
 * of specific Exception types: "Stuff happens." If you actually <i>need</i> to handle specific Exception
 * types specifically, this is not the right tool for the job. It will obfuscate &amp; confuse that handling.
 * <br>
 * Rather than writing one-for-one replacements for each of the java.util.function interfaces (and
 * eventually all the things that use them, and so on), we have only two, to handle "returns a value" and
 * "doesn't return a value". The idea is to use them <i>inside</i> the "canonical" lambdas; i.e. a shortcut for:
  <pre>
    try {....} catch (Exception e) {throw new RuntimeException(e);}
  </pre>
 * Note however that if we catch RuntimeException, we just rethrow it directly since no wrapper is needed.
 */
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
