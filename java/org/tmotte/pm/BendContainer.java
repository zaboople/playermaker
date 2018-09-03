package org.tmotte.pm;
import java.util.List;

/**
 * Background: In Midi a bend can go a "whole step" by default, which is to say, two notes up or down.
 * However, you can change the "Bend sensitivity" to increase the range from a whole step to many more
 * steps. Internally, the 16383/8192/0 limits remain the same. Refer to {@link Player#setBendSensitivity(int}
 * for this setting.
 * <br>
 * Also in Midi, bends apply to the whole channel. This is rather inflexible, so internally we make use of "spare"
 * channels when bends are applied differently to simultaneous notes (refer to Chord#n).
 * When using multiple Players and bending notes, you should assign them channels with gaps
 * in between, e.g. for three Players you might assign 0, 3, 6 instead of 0, 1, 2. This would give the first two
 * players two extra channels, and the 3rd player all of the rest (except channel 10, which can only play drums).
 * Refer to {@link Player#channel}.
 * <br>
 * Note that bends & vibratos are done sequentially, so you can do
     <pre>
     chord.bend(...).bend(....)..vibrato(...).bend(...)
     </pre>
   and each bend or vibrato will happen after the previous. So, no, you can't do vibrato & bend at the same
   time, although that is a technically feasible feature.
 * <p>
 * (Note: This is an interface because I want default methods i.e. multiple inheritance. However, some
 * things that shouldn't be public are, because interfaces are that way. And I am a bad person.)
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

    /**
     * Aside from using Player.setPressure(), this gives a more fine-tuned variation.
     * Note that for delay/duration/frequency, you can use other overloads that allow
     * you to provide a decimal value as usual, (e.g. 8., 8.3).
     *
     * @param delay Time to wait before delay
     * @param duration The period of duration for the vibrato
     * @param frequency The speed of the vibrato, expressed as a duration (larger numbers are faster).
     * @param denominator The pitch variation of the vibrato; lower gives more variation,
     *    as determined by <code>variation=pitch_sensitivy/denominator</code>
     */
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
        Long long0=0l;//Avoids java 10 compiler warning
        return vibrato(long0, duration, frequency, denominator);
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