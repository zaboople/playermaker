package org.tmotte.pm;
import java.util.List;

/**
 * This is an interface because I want default methods i.e. multiple inheritance.
 * FIXME what isn't this for ZBend
 * <p>
 * Background: By default, a bend can go a "whole step", which is to say, two notes up or down.
 * 0 is all the way down, and 16384-1 is all the way up. So, 8192 is no bend at all.
 * Bends apply to the whole channel, regardless of what track they appear on. Finally,
 * the bend stays applied until it is unapplied.
 * <p>
 * However, you can change the "Bend sensitivity" (refer to the Player API) to increase
 * the range from a whole step to many more steps. The 16383/8192/0 limits remain the same.
 */
public interface BendContainer<T> {
    List<Bend> getBends();
    void setBends(List<Bend> bends);
    long totalDuration();
    T self();

    public default T bend(int denominator) {
        return bend(0L, totalDuration(), denominator);
    }
    public default T bend(long duration, int denominator) {
        return bend(0L, duration, denominator);
    }
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
        Bend.add2(this, delay, duration, denominator);
        return self();
    }

    public default T bend(double duration, int denominator) {
        return bend(0D, duration, denominator);
    }
    public default T bend(double delay, double duration, int denominator) {
        Bend.add2(this, delay, duration, denominator);
        return self();
    }

    public default T bend(int duration, int denominator) {
        return bend(0, duration, denominator);
    }
    public default T bend(int delay, int duration, int denominator) {
       Bend.add2(this, delay, duration, denominator);
       return self();
    }



    public default T vibrato(long frequency, int denominator) {
        return vibrato(0L, totalDuration(), frequency, denominator);
    }
    public default T vibrato(long duration, long frequency, int denominator) {
        return vibrato(0L, duration, frequency, denominator);
    }
    public default T vibrato(long delay, long duration, long frequency, int denominator) {
        Bend.vibrato2(this, delay, duration, frequency, denominator);
        return self();
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
    public default T vibrato(double duration, double frequency, int denominator) {
        return vibrato(0D, duration, frequency, denominator);
    }
    public default T vibrato(double delay, double duration, double frequency, int denominator) {
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }

    //FIXME this and other variations.... Maybe just do Number
    public default T vibrato(int delay, double duration, double frequency, int denominator) {
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }


}