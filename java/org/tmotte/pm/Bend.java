package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * Background: By default, a bend can go a "whole step", which is to say, two notes up or down.
 * 0 is all the way down, and 16384-1 is all the way up. So, 8192 is no bend at all.
 * Bends apply to the whole channel, regardless of what track they appear on. Finally,
 * the bend stays applied until it is unapplied.
 * <p>
 * However, you can change the "Bend sensitivity" (refer to the Player API) to increase
 * the range from a whole step to many more steps. The 16383/8192/0 limits remain the same.
 *
 * @see Player#setBendSensitivity(int)
 */
public final class Bend {
    final long delay;
    final long duration;
    final int denominator;

    /**
     * @param delay A period to wait before the bend; this can be expressed as
     *        2/4/8/16/32/64 etc to indicate a period corresponding to half/quarter/eighth/etc
     *        notes (for triplet and dotted note delays, use Bend(double, double, int)).
     * @param denominator Indicates the 1/denominator
     * of our bend range to go up or down. So, if our bend sensitivity is set to the
     * default of one whole step:
       <ul>
         <li>1 is a whole step, e.g. C to D
         <li>2 is a half step, e.g. C to C#
         <li>4 is a quarter step (obviously off key but it's jazzy that way)
       </ul>
     * And so forth.
     */
    public Bend(long delay, long duration, int denominator) {
        this.delay=Divisions.convert(delay);
        this.duration=Divisions.convert(duration);
        this.denominator=denominator;
    }

    /**
     * Same as Bend(long, long, int) but the doubles are for expressing
     * <ul>
     *   <li>Triplets - e.g. 8.3 means a single eighth-note from a triplet
     *   <li>Dotted notes - e.g. 8.2 means a dotted eighth-note. FIXME
     *      how to delay/duration 8.3 but twice???? 16.6??? 8.6???
     * <li>
     */
    public Bend(double delay, double duration, int denominator) {
        this.delay=Divisions.convert(delay);
        this.duration=Divisions.convert(duration);
        this.denominator=denominator;
    }

    public long delay() {
        return delay;
    }
    /** This is not the duration as input, but converted to tick groups. FIXME rename durationTicks */
    public long duration() {
        return duration;
    }
    public int denominator() {
        return denominator;
    }

    public static List<Bend> add(List<Bend> bends, long delay, long duration, int denominator) {
        if (bends==null)
            bends=new ArrayList<>();
        bends.add(new Bend(delay, duration, denominator));
        return bends;
    }
    public static List<Bend> add(List<Bend> bends, double delay, double duration, int denominator) {
        if (bends==null)
            bends=new ArrayList<>();
        bends.add(new Bend(delay, duration, denominator));
        return bends;
    }

}