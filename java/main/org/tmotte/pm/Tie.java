package org.tmotte.pm;

/**
 * Represents the duration of "tied" notes. The Chord class already has a {@link Chord#t(Number)}
 * method that will do the job, but this is an alternative.
 * <p>
 * Sometimes instead of just the usual half/quarter/eighth/dotted/triplet note, we want to send a
 * tied-note duration of, say, quarter + eighth, or three sixteenths, or whatever. Tie allows you
 * to create a single duration out of several. Refer to test.hear.TestTies2 for an example.
 * <p>
 * For convenience, you can <code>import static Tie.tie</code>.
 * <P>
 * Note that Tie extends Number just so that we don't have to overload the heck out of all those
 * Chord.bend() etc. methods with yet another type. Tie will throw an Exception if you call any
 * of Number's abstract methods (e.g. Number.doubleValue()) because they serve no useful
 * purpose and there's no "implied" meaning for those methods here.
 */
public class Tie extends Number {
    private static final long serialVersionUID = 1L;

    /** A shortcut to <code>new Tie(Number...)</code> */
    public static Tie tie(Number... durations) {
        return new Tie(durations);
    }

    /** Constant, durations passed to constructor. */
    final Number[] durations;

    /**
    * Creates a tied-note duration that can be passed to any method that expects a Number as a
    * duration. The durations should follow all the usual rules about dotted, triplet etc notes.
    */
    public Tie(Number... durations) {
        this.durations=durations;
    }

    /** Always throws UnsupportedOperationException */
    public @Override double doubleValue() {
        throw new UnsupportedOperationException();
    }
    /** Always throws UnsupportedOperationException */
    public @Override float floatValue() {
        throw new UnsupportedOperationException();
    }
    /** Always throws UnsupportedOperationException */
    public @Override int intValue() {
        throw new UnsupportedOperationException();
    }
    /** Always throws UnsupportedOperationException */
    public @Override long longValue() {
        throw new UnsupportedOperationException();
    }

}
