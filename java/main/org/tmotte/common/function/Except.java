package org.tmotte.common.function;

/**
 * This is a lambdafication tool for dealing with the typical java.util.function functional interfaces
 * and their refusal to allow checked exceptions. <code>Except</code> handles checked exceptions by wrapping them
 * in <code>RuntimeException</code>, which means things will still blow up, but there's no need for try-catch; after all,
 * "Stuff happens."
 * <p>
 * If you actually <i>need</i> to handle specific Exception types specifically, this is not the right tool for the job.
 * It will tend to obfuscate &amp; confuse that handling.
 * <p>
 * Rather than writing <em>replacements</em> for the java.util.function interfaces (and
 * eventually all the things that use them, and so on), we have only two: Except.get() to handle "returns a value", and
 * Except.run() for "doesn't return a value". The idea is to use them <i>inside</i> the "canonical" lambdas; i.e. instead of this:
  <pre>

    ...method(...->try {....} catch (Exception e) {throw new RuntimeException(e);});
  </pre>
  * We could do one of these:
  <pre>

    ...method(() ->  Except.run(() -> ...));
    ...method(... -> Except.get(() -> ...));
  </pre>
 * The result is not super-awesome, but somewhat more concise.
 * <p>
 * Note that if Except catches a RuntimeException, it rethrows that exception directly since no wrapper is needed.
 */
public class Except {
    private Except(){}

    /** Allows a Runnable that can throw Exceptions */
    public static interface ExceptionalRunnable {
        /** Your lambda
            @throws Exception for any reason
        */
        public void run() throws Exception;
    }
    /** Allows a Supplier that can throw Exceptions
        @param <T> Class of your lambda's return value.
    */
    public static interface ExceptionalSupplier<T> {
        /** Your lambda
            @throws Exception for any reason
            @return Whatever the lambda returns
        */
        public T get() throws Exception;
    }
    /** Use with your own lambda to eliminate "throws" clauses.
        @param er The lambda
    */
    public static void run(ExceptionalRunnable er) {
        try {
            er.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /** Use with your own lambda that returns a value to
        eliminate "throws" clauses.
        @param s The lambda
        @param <T> Type of the lambda's return value
        @return Whatever the lambda returns
    */
    public static <T> T get(ExceptionalSupplier<T> s) {
        try {
            return s.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
