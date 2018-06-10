package org.tmotte.pm;
import java.util.List;

/**
 * Same as Bend(int, int, int) but the doubles are for expressing
 * <ul>
 *   <li>Triplets - e.g. 8.3 means a single eighth-note from a triplet
 *   <li>Dotted notes - e.g. 8.2 means a dotted eighth-note. FIXME
 *      how to delay/duration 8.3 but twice???? 16.6??? 8.6???
 * <li>
 * FIXME
 */

/**
 * (This is an interface because I want default methods i.e. multiple inheritance. However, some
 * things that shouldn't be public are, because interfaces are that way. And I am a bad person.)
 * <p>
 * Background: By default, a bend can go a "whole step", which is to say, two notes up or down.
 * 0 is all the way down, and 16384-1 is all the way up. So, 8192 is no bend at all.
 * Bends apply to the whole channel, regardless of what track they appear on. Finally,
 * the bend stays applied until it is unapplied.
 * <p>
 * However, you can change the "Bend sensitivity" to increase
 * the range from a whole step to many more steps. The 16383/8192/0 limits remain the same.
 *
 * @see Player#setBendSensitivity(int)
 */
public interface BendContainer<T> {

    /**
     * Internal Use. (We can't have an internal variable unless it's final, which is dumb. Being obsessed with
     * memory, then, implementors maintain a List<Bend> and initialize when they need to.)
     */
    List<Bend> makeBends();
    /* Internal use */
    long totalDuration();
    /* Internal use */
    T self();

    ///////////
    // BEND: //
    ///////////

    /**
     * @param delay A period to wait before the bend; this can be expressed as
     *        2/4/8/16/32/64 etc to indicate a period corresponding to half/quarter/eighth/etc
     *        notes (for triplet and dotted note delays, use Bend(double, double, int)).
     * @param duration The time over which the bend takes place; if this is shorter than the length
     *        of the given Note/Chord, the pitch remains constant for the rest of the Note/Chord's duration.
     * @param denominator Can be negative or positive. Indicates the 1/denominator of our bend range to go
     * up or down. So, if our bend sensitivity is set to the default of one whole step:
       <ul>
           <li>1 is a whole step, e.g. C to D
           <li>2 is a half step, e.g. C to C#
           <li>4 is a quarter step (obviously off key but it's jazzy that way)
       </ul>
     * And so forth.
     * The denominator must be divisible by 2, but it's okay for it to be larger than our bend sensitivity.
     */
    public default T bend(long delay, long duration, int denominator) {
        Bend.add(makeBends(), delay, duration, denominator);
        return self();
    }
    public default T bend(long duration, int denominator) {
        return bend(0L, duration, denominator);
    }


    public default T bend(int denominator) {
        return bend(0L, totalDuration(), denominator);
    }
    public default T bend(int duration, int denominator) {
        return bend(0, duration, denominator);
    }
    public default T bend(int delay, int duration, int denominator) {
       Bend.add(makeBends(), delay, duration, denominator);
       return self();
    }
    public default T bend(Number delay, Number duration, int denominator) {
       return bend(Divisions.convert(delay), Divisions.convert(duration), denominator);
    }


    public default T bend(double duration, int denominator) {
        return bend(0D, duration, denominator);
    }
    public default T bend(double delay, double duration, int denominator) {
        Bend.add(makeBends(), delay, duration, denominator);
        return self();
    }

    //////////////
    // VIBRATO: //
    //////////////

    public default T vibrato(long delay, long duration, long frequency, int denominator) {
        Bend.vibrato(makeBends(), delay, duration, frequency, denominator);
        return self();
    }


    public default T vibrato(long frequency, int denominator) {
        return vibrato(0L, totalDuration(), frequency, denominator);
    }
    public default T vibrato(long duration, long frequency, int denominator) {
        return vibrato(0L, duration, frequency, denominator);
    }

    public default T vibrato(int frequency, int denominator) {
        return vibrato(0L, totalDuration(), Divisions.convert(frequency), denominator);
    }
    public default T vibrato(int duration, int frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public default T vibrato(int delay, int duration, int frequency, int denominator) {
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }

    public default T vibrato(double frequency, int denominator) {
        return vibrato(0L, totalDuration(), Divisions.convert(frequency), denominator);
    }
    public default T vibrato(Number duration, Number frequency, int denominator) {
        return vibrato(new Long(0), duration, frequency, denominator);
    }


    public default T vibrato(Number delay, Number duration, Number frequency, int denominator) {
        //System.out.println("BendContainer.vibrato(Number, Number, Number, int)");
        //System.out.println("BendContainer.vibrato("+delay+", "+duration+", "+frequency+", "+denominator+")");
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }

}