package org.tmotte.pm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Consumer;
import org.tmotte.common.text.Log;

/**
 * Represents a chord, be it one note or many. While a chord will often sound all of its
 * notes at once and finish them all at once, overlapping/arpeggiating possibilities are
 * allowed via the r() method, which creates a Rest, and then delayed notes can be added.
 * <p>
 * Note: Refer to <a href="./package-summary.html">the org.tmotte.pm package summary</a> for an explanation
 * of note durations &amp; pitches.
 * @param <T> Represents the class of the parent obtained from Chord.up(); can be a Player, a Chord,
 *   or nested Chord instances. This makes it easy to create lengthy method chains.
 */
public class Chord<T> extends NoteAttributeHolder<Chord<T>> {


    private final T parent;
    private final List<Integer> pitches=new ArrayList<>();
    private final long restBefore;
    private NoteAttributes attributes;
    private boolean usingParentAttributes=true;
    private List<Swell> swells=null;
    private List<Bend> bends=null;
    private List<Chord<Chord<T>>> subChords=null;
    private long duration;
    private boolean bendWithParent=false;
    private boolean isTrueRest=false;
    private String tag;


    protected Chord(T parent, NoteAttributes attributes, long duration, int... pitches) {
        this(parent, attributes, 0L, duration, pitches);
    }
    private Chord(T parent, NoteAttributes attributes, long restBefore, long duration, int... pitches) {
        this.parent=parent;
        this.attributes=attributes;
        this.restBefore=restBefore;
        this.duration=duration;
        for (int p: pitches)
            this.pitches.add(p);
        Log.log("Chord", "Parent {} pitches {} restBefore {} duration {}", parent, this.pitches, restBefore, duration);
    }

    /**
     * Takes us back to the original Player/Chord, for the sake of "fluent" programming.
     * @return this
     */
    public T up() {
        return parent;
    }

    /** Only for use with sub-chords created with rest(); indicates that
        that the sub-chord should be bent in the same fashion as its parent.
        This should be used to share vibrato as well, since vibrato is bending.
     * @return this
     */
    public Chord<T> bendWithParent() {
        if (parent==null)
            throw new IllegalStateException("No parent to bend with");
        bendWithParent=true;
        return this;
    }

    /** A shortcut to {@link bendWithParent()}
     * @return this
     */
    public Chord<T> bwp() {
        return bendWithParent();
    }

    /** For debugging purposes only; will print the tag when debugging info for the chord is printed.
     * @param t The tag name
     * @return this
     */
    public Chord<T> tag(String t) {
        this.tag=t;
        return this;
    }

    public String tag() {
        return tag;
    }

    ///////////////////////////////
    // TIES, RESTS & SUB-CHORDS  //
    // AKA t() r() & c():        //
    ///////////////////////////////


    /**
     * Increases the duration of this Chord by "tying" it to <code>duration</code>.
     * <br>
     * Also consider using the {@link Tie} class, which is a Number and can be used to
     * to create tied durations.
     * @param duration A duration expressed in the typical notation.
     * @return this
     * @see Tie
     */
    public Chord<T> t(Number duration) {
        return t(Divisions.convert(duration));
    }
    private Chord<T> t(long duration) {
        this.duration+=duration;
        return this;
    }

    /**
     * Allows one to add delayed, overlapping Chords to the original Chord.
     * Use {@link Rest#c(Number, int...)}, etc. methods for adding these delayed notes.
     * This is similar to the notes &amp; staves practice of placing a rest above/below
     * a note to indicate an amount of time to wait before playing a parallel note.
     * <p>
     * Rest.c() will return a Chord that is a <i>delayed</i> sub-chord of the original
     * Chord. From there you can call the usual Chord methods on that sub-chord,
     * finishing with .up() to take you back to the original Chord. So really,
     * Chord.r().c() does the same thing as Chord.c(), but delaying the sub-chord.
     *
     * @param duration A duration expressed in the typical notation.
     * @return A Rest object that is aware of this Chord, so that Rest.c()
     *   returns a delayed sub-chord of this Chord.
     */
    public Rest<T> r(Number duration) {
        return rest(Divisions.convert(duration));
    }
    /**
     * Same as r(Number) but allows tied-note rest by giving multiple durations.
     * @param durations More than one duration can be given.
     * @return same as r(Number)
     */
    public Rest<T> r(Number... durations) {
        return rest(Divisions.convert(Tie.tie(durations)));
    }
    private Rest<T> rest(long duration) {
        return new Rest<>(this, duration);
    }

    // Only used by Player, for a "real" rest
    Chord<T> setTrueRest() {
        isTrueRest=true;
        return this;
    }
    boolean isTrueRest() {
        return isTrueRest;
    }

    /**
     * Allows us to add a chord that plays at the same time as this one,
     * but that is not subject to the same bend/vibrato/swell/etc. effects
     * unless you use bendWithParent(). Use r() before c() to add a rest/delay
     * before this chord.
     * @param duration Standard duration notation
     * @param notes Standard note notation
     * @return a sub-chord of this Chord
     */
    public Chord<Chord<T>> c(Number duration, int... notes) {
        return addChord(0L, Divisions.convert(duration), notes);
    }
    /** A shortcut to <code>c(Number, int...).up()</code>
     * @param duration Standard duration notation
     * @param notes Standard note notation
     * @return this
     */
    public Chord<T> up(Number duration, int... notes) {
        return c(duration, notes).up();
    }


    /** Exposed for use by Rest, which will supply a non-zero restBefore */
    protected Chord<Chord<T>> addChord(long restBefore, long duration, int... pitches) {
        var n=new Chord<>(this, attributes, restBefore, duration, pitches);
        if (subChords==null) subChords=new ArrayList<>();
        subChords.add(n);
        return n;
    }

    /////////////
    // SWELLS: //
    /////////////

    /**
     * Does a volume "swell", where the volume is raised or lowered to <code>toVolume</code>
     * over <code>duration</code> after waiting for <code>delay</code>
     * @param delay Time to delay before swell, as standard time notation
     * @param duration Duration of swell itself
     * @param toVolume Volume value from 0-127 - can go up or
     * @return The same Chord instance
     */
    public Chord<T> swell(Number delay, Number duration, int toVolume) {
        return swell(Divisions.convert(delay), Divisions.convert(duration), toVolume);
    }

    /** A shortcut to <code>swell(0, duration, toVolume)</code> */
    public Chord<T> swell(Number duration, int toVolume) {
        return swell(0, duration, toVolume);
    }

    /**
     * A shortcut to <code>swell(0, duration, volume)</code> where <code>duration</code>
     * is all time left in the chord after previous swells are accounted for, since swells
     * are sequential. This is similar to the <code>bend()</code> method's calculations.
     */
    public Chord<T> swell(int toVolume) {
        final long sd=
            totalDuration() -
            makeSwells().stream()
                .map(
                    swell->swell.delay()+swell.duration()
                )
                .reduce(Long::sum)
                .orElse(0L);
        return swell(0L, sd, toVolume);
    }

    private Chord<T> swell(long delay, long duration, int toVolume) {
        makeSwells().add(new Swell(delay, duration, toVolume));
        return this;
    }

    private List<Swell> makeSwells() {
        return swells==null
            ?swells=new ArrayList<>()
            :swells;
    }

    ////////////
    // BENDS: //
    ////////////

    /**
     * Bends a note, as can be done on instruments like the guitar.
     * @param delay A period to wait before the bend; this can be expressed as
     *        2/4/8/16/32/64 etc to indicate a period corresponding to half/quarter/eighth/etc
     *        notes, or 8.3 for triplet and 8. for dotted notes. Note that multiple bends and
     *        vibratos are scheduled <i>consecutively</i>, never simultaneously, so there is
     *        no need to add in your own delay to account for this; the next bend picks up
     *        where the previous left off.
     * @param duration The duration over which the bend takes place, expressed in the same
     *        notation as delay; if this is shorter than the length of the given Note/Chord, the
     *        pitch remains constant for the rest of the Note/Chord's duration.
     * @param denom Denominator: Can be negative or positive. Indicates the 1/denominator of our
     *        bend range to go up or down. So, if our bend sensitivity is set to the default of
     *        one whole step (which is to say, 2 semitones):
       <ul>
           <li>1 is a whole step, e.g. C to D
           <li>2 is a half step, e.g. C to C#
           <li>4 is a quarter step (obviously off key but it's jazzy that way)
       </ul>
     * ... and so forth.
     * <br>
     * Note: The denominator must be divisible by 2!
     * @return this
     * @see Player#setBendSensitivity(int)
     */
    public Chord<T> bend(Number delay, Number duration, int denom) {
       return bend(Divisions.convert(delay), Divisions.convert(duration), denom);
    }

    /**
     * A shortcut to bend(0, duration, denom) (that is, 0 delay).
     */
    public Chord<T> bend(Number duration, int denom) {
        return bend(0L, Divisions.convert(duration), denom);
    }
    /**
     * A shortcut for a bend spread across the entire duration of the chord with no delay,
     * i.e. chord.bend(delay=0, [chord duration], denom). However, if there are already bends
     * in the Chord, then this bend will only take the time remaining.
     */
    public Chord<T> bend(int denom) {
        return bend(0L, duration-bendDuration(), denom);
    }

    private Chord<T> bend(long delay, long duration, int denominator) {
        Bend.add(makeBends(), delay, duration, denominator);
        return this;
    }

    //////////////
    // VIBRATO: //
    //////////////

    /**
     * This gives a more fine-tuned vibrato/tremolo than Player.setPressure().
     *
     * @param delay Duration to wait before starting vibrato (can be 0). As with bend(), there
     *    is no need to add extra delay to avoid overlapping bends/vibratos, since they are
     *    always consecutive (you can't yet bend and vibrato at the same time, sorry).
     * @param duration The duration of the vibrato
     * @param freq The frequency/speed of the vibrato, expressed as a duration (larger numbers are faster).
     * @param denom The "denominator" of pitch variation of the vibrato, which works the same as
     *    for bends: lower gives more variation, as determined by
        <pre>
            variation = bend sensitivity / denom
        </pre>
     * @return this
     * @see Player#setBendSensitivity(int)
     */
    public Chord<T> vibrato(Number delay, Number duration, Number freq, int denom) {
        //log("vibrato(Number, Number, Number, int)");
        //log("vibrato("+delay+", "+duration+", "+freq+", "+denominator+")");
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(freq),
            denom
        );
    }

    /**
     * A shortcut that assumes the vibrato should have 0 delay and use up all remaining time
     * for the Chord. Vibratos are treated internally as bends, and like bends, they happen
     * sequentially, so the actual start and duration depends on how many vibratos/bends
     * preceded this one.
     */
    public Chord<T> vibrato(Number freq, int denom) {
        return vibrato(0L, duration-bendDuration(), Divisions.convert(freq), denom);
    }
    /**
     * Another shortcut to vibrato(), this time only assuming 0 delay.
     */
    public Chord<T> vibrato(Number duration, Number freq, int denom) {
        return vibrato(0L, duration, freq, denom);
    }

    private Chord<T> vibrato(long delay, long duration, long frequency, int denominator) {
        Bend.vibrato(makeBends(), delay, duration, frequency, denominator);
        return this;
    }

    private List<Bend> makeBends() {
        return bends==null
            ?bends=new ArrayList<>()
            :bends;
    }

    private long bendDuration() {
        long time=makeBends().stream().reduce(
            0L, (n, bend)->n+bend.delay()+bend.duration(), (x, y)->x+y
        );
        return time;
    }


    ////////////////
    //            //
    // INTERNALS: //
    //            //
    ////////////////

    protected long totalDuration() {
        return duration +
            (subChords==null
                    ?0
                    :subChords.stream().map(Chord::totalDuration)
                        .reduce(
                            0L,
                            (x,y)-> y>x ?y :x
                        )
            );
    }

    protected List<Integer> pitches() {
        return pitches;
    }
    protected List<Chord<Chord<T>>> chords() {
        return subChords==null ?Collections.emptyList() :subChords;
    }
    protected List<Bend> bends() {
        return bends==null ?Collections.emptyList() :bends;
    }
    protected List<Swell> swells() {
        return swells==null ?Collections.emptyList() :swells;
    }


    protected long duration() {
        return duration;
    }
    protected long restBefore() {
        return restBefore;
    }
    protected boolean isBendWithParent() {
        return bendWithParent;
    }


    /////////////////////////////////////////
    // INTERNAL LOGIC FOR NoteAttributes : //
    /////////////////////////////////////////

    protected @Override NoteAttributes getNoteAttributesForRead(){
        return attributes;
    }
    protected @Override Chord<T> setVolume(int v) {
        getAttributesForWrite().volume=v;
        return this;
    }
    protected @Override Chord<T> setTranspose(int semitones) {
        getAttributesForWrite().transpose=semitones;
        return this;
    }

    private NoteAttributes getAttributesForWrite() {
        if (usingParentAttributes) {
            usingParentAttributes=false;
            attributes=new NoteAttributes(attributes);
        }
        return attributes;
    }

}
