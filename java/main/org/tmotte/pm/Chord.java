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
 * FIXME test much overlapping waxing/waning etc.
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


    public Chord<T> bendWithParent() {
        bendWithParent=true;
        return this;
    }

    /**
     * Takes us back to the original Player/Chord, for the sake of "fluent" programming.
     */
    public T up() {
        return parent;
    }


    ///////////////////////////////
    // TIES, RESTS & SUB-CHORDS  //
    // AKA t() r() & c():        //
    ///////////////////////////////


    /**
     * Use to create "tied" notes, for example if you wanted to
     * extend an quarter note by a sixteenth, you could write
        <pre>
        player.c(4, C).t(16).up()
        </pre>
        Use {@link t(double)} for dotted & triplet notes.
     * @param duration A time period expressed in the typical notation.
     */
    public Chord<T> t(int duration) {
        return t(Divisions.convert(duration));
    }
    public Chord<T> t(double duration) {
        return t(Divisions.convert(duration));
    }
    private Chord<T> t(long duration) {
        this.duration+=duration;
        return this;
    }

    /**
     * Allows one to add delayed, overlapping notes to the Chord.
     * {@link Rest} implements Notable, so it has a {@link Rest#n(int, int)},
     * {@link Rest#c(int, int)}, etc. methods for adding notes. This is similar
     * to the notes &amp; staves practice of placing a rest above/below a note
     * to indicate an amount of time to wait before playing a parallel note.
     * @param A standard notation integer duration: 4 for a quarter rest, 8
     *        for an eighth rest, etc.
     */
    public Rest<T> r(int i) {return rest(Divisions.convert(i));}
    /**
     * Does the same as {@link #r(int)} but allowing for triplet &amp; dotted-note
     * values.
     */
    public Rest<T> r(double d) {return rest(Divisions.convert(d));}
    private Rest<T> rest(long duration) {
        return new Rest<>(this, duration);
    }

    public Chord<Chord<T>> c(int duration, int... notes) {
        return addChord(0L, Divisions.convert(duration), notes);
    }
    public Chord<Chord<T>> c(double duration, int... notes) {
        return addChord(0L, Divisions.convert(duration), notes);
    }

    /** A shortcut to c(int, int...).up() */
    public Chord<T> up(int duration, int... notes) {
        return c(duration, notes).up();
    }
    /** A shortcut to c(double, int...).up() */
    public Chord<T> up(double duration, int... notes) {
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

    public Chord<T> swell(int toVolume) {
        return swell(0L, duration, toVolume);
    }
    public Chord<T> swell(int duration, int toVolume) {
        return swell(0, duration, toVolume);
    }
    public Chord<T> swell(double duration, int toVolume) {
        return swell(0, duration, toVolume);
    }
    public Chord<T> swell(int delay, int duration, int toVolume) {
        return swell(Divisions.convert(delay), Divisions.convert(duration), toVolume);
    }
    public Chord<T> swell(double delay, double duration, int toVolume) {
        return swell(Divisions.convert(delay), Divisions.convert(duration), toVolume);
    }
    public Chord<T> swell(double delay, int duration, int toVolume) {
        return swell(Divisions.convert(delay), Divisions.convert(duration), toVolume);
    }
    public Chord<T> swell(int delay, double duration, int toVolume) {
        return swell(Divisions.convert(delay), Divisions.convert(duration), toVolume);
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
     * @param delay A period to wait before the bend; this can be expressed as
     *        2/4/8/16/32/64 etc to indicate a period corresponding to half/quarter/eighth/etc
     *        notes, or 8.3 for triplet and 8. for dotted notes.
     * @param duration The time over which the bend takes place, expressed in the same notation
     *        as delay; if this is shorter than the length
     *        of the given Note/Chord, the pitch remains constant for the rest of the Note/Chord's duration.
     * @param denominator Can be negative or positive. Indicates the 1/denominator of our bend range to go
     *        up or down. So, if our bend sensitivity is set to the default of one whole step (which is to say,
     *        2 semitones):
       <ul>
           <li>1 is a whole step, e.g. C to D
           <li>2 is a half step, e.g. C to C#
           <li>4 is a quarter step (obviously off key but it's jazzy that way)
       </ul>
     * ... and so forth.
     * <br>
     * The denominator must be divisible by 2.
     */
    public Chord<T> bend(Number delay, Number duration, int denominator) {
       return bend(Divisions.convert(delay), Divisions.convert(duration), denominator);
    }

    public Chord<T> bend(int delay, int duration, int denominator) {
       Bend.add(makeBends(), delay, duration, denominator);
       return this;
    }
    /**
     * A shortcut to bend(0, duration, denominator) (that is, 0 delay).
     */
    public Chord<T> bend(int duration, int denominator) {
        return bend(0, duration, denominator);
    }
    /**
     * A shortcut for a bend spread across the entire duration of the note with no delay,
     * i.e. bend(0, <duration>, denominator.
     * FIXME test this
     */
    public Chord<T> bend(int denominator) {
        return bend(0L, duration, denominator);
    }

    /** FIXME test and verify we need the doubles, probably don't */
    public Chord<T> bend(double delay, double duration, int denominator) {
        Bend.add(makeBends(), delay, duration, denominator);
        return this;
    }
    public Chord<T> bend(double duration, int denominator) {
        return bend(0D, duration, denominator);
    }

    private Chord<T> bend(long delay, long duration, int denominator) {
        Bend.add(makeBends(), delay, duration, denominator);
        return this;
    }
    private Chord<T> bend(long duration, int denominator) {
        return bend(0L, duration, denominator);
    }

    //////////////
    // VIBRATO: //
    //////////////

    /**
     * Aside from using Player.setPressure(), this gives a more fine-tuned variation.
     * Note that for delay/duration/frequency, you can use other overloads that allow
     * you to provide a decimal value for dotted notes &amp; triplets as usual, (e.g. 8., 8.3).
     *
     * @param delay Time to wait before delay
     * @param duration The period of duration for the vibrato
     * @param frequency The speed of the vibrato, expressed as a duration (larger numbers are faster).
     * @param denominator The pitch variation of the vibrato, which works the same as for bends: lower
     *    gives more variation, as determined by <code>variation=bend_sensitivy/denominator</code>.
     */
    public Chord<T> vibrato(Number delay, Number duration, Number frequency, int denominator) {
        //log("vibrato(Number, Number, Number, int)");
        //log("vibrato("+delay+", "+duration+", "+frequency+", "+denominator+")");
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }

    public Chord<T> vibrato(int frequency, int denominator) {
        return vibrato(0L, duration, Divisions.convert(frequency), denominator);
    }
    public Chord<T> vibrato(int duration, int frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Chord<T> vibrato(int delay, int duration, int frequency, int denominator) {
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }

    public Chord<T> vibrato(double frequency, int denominator) {
        return vibrato(0L, duration, Divisions.convert(frequency), denominator);
    }
    public Chord<T> vibrato(Number duration, Number frequency, int denominator) {
        Long long0=0l;//Avoids java 10 compiler warning
        return vibrato(long0, duration, frequency, denominator);
    }


    private Chord<T> vibrato(long delay, long duration, long frequency, int denominator) {
        Bend.vibrato(makeBends(), delay, duration, frequency, denominator);
        return this;
    }
    private Chord<T> vibrato(long frequency, int denominator) {
        return vibrato(0L, duration, frequency, denominator);
    }
    private Chord<T> vibrato(long duration, long frequency, int denominator) {
        return vibrato(0L, duration, frequency, denominator);
    }

    private List<Bend> makeBends() {
        return bends==null
            ?bends=new ArrayList<>()
            :bends;
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

    /**
        FIXME why not do away with this garbage? Now we have the ability
        to specify subchords separately, so we only need to create a new
        parent attributes if we are using parent attributes. Most of the time
        we would set volume/etc. before creating sub-chords, in which case
        they will inherit. If we set it after creating them, they shouldn't.

       This is tricky because Notes are created immediately for the
       chord as well as after (via Chord.n()) so we may or may not
       want the notes to inherit attributes. Complicating this is:
       1. We start with the Player's attributes, which we must not change.
          So we have to create a new NoteAttributes if we are still using
          the NoteAttributes we got from Player.
       2. We want Sub-Chords to get our changes, but we just made
          a new NoteAttributes, so we'll tell them to use it instead of
          the old one they already have.
       3. But if the Note already customized its attributes... we'll leave
          their Attributes object alone, but still pass on the individual
          attribute value to them and tell them to accept it.
    */
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
