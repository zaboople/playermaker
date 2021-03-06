package org.tmotte.pm;

/**
 * Represents the duration of "tied" notes. The Chord class already has a {@link Chord#tie(Number)} method
 * that will do the job, but sometimes, instead of just the usual half/quarter/eighth/dotted/triplet note, we want to send
 * a tied-note duration to Chord.bend(), Chord.vibrato(), Chord.swell(), etc. for delay, duration, etc. In those
 * (possibly rare) cases you'll need Tie.
 * <p>
 * For convenience, you can <code>import static Tie.tie</code>.
 * <P>
 * Note that Tie extends Number just so that we don't have to overload the heck out of all those Chord.bend() etc. methods
 * with yet another numeric type. Tie will throw an Exception if you call any of Number's abstract methods (e.g. Number.doubleValue())
 * because they serve no useful purpose and there's no "implied" meaning for those methods here.
 */
public class Tie extends Number {

  /** A short to <code>new Tie(Number...)</code> */
  public static Tie tie(Number... durations) {
    return new Tie(durations);
  }

  final Number[] durations;

  /**
   * Creates a tied-note duration that can be passed to any method that expects a Number as a duration. The durations
   * should follow all the usual rules about dotted, triplet etc notes.
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