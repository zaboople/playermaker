package org.tmotte.pm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Consumer;

/**
 * Represents a chord, be it one note or many. While a chord will often sound all of its
 * notes at once and finish them all at once, overlapping/arpeggiating possibilities are
 * allowed via the r() method, which creates a Rest, and then delayed notes can be added.
 * FIXME test much overlapping waxing/waning etc.
 */
public class Chord<T> extends NoteAttributeHolder<Chord<T>> implements BendContainer<Chord<T>> {


    private final T parent;
    private final List<Integer> pitches=new ArrayList<>();
    private final long restBefore;
    private NoteAttributes attributes;
    private boolean usingParentAttributes=true;
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


    ////////////////////////////////////
    // PUBLIC BUT INTERNAL OVERRIDES: //
    // ALL FOR BEND CONTAINER FIXME   //
    ////////////////////////////////////


    /** For internal use, required by BendContainer */
    public @Override Chord<T> self() {
        return this;
    }

    /** For internal use, required by BendContainer */
    public @Override List<Bend> makeBends() {
        if (bends==null)
            bends=new ArrayList<>();
        return bends;
    }

    /**
     * Mostly for internal use; obtains the total duration of the Chord by our internal (not midi) tick system,
     * not in the general notation used for Chord/Note input durations.
     */
    public @Override long durationForBend() {
        return duration;
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
        passOnToChords(x-> x.setVolume(v));
        this.attributes.volume=v;
        return this;
    }
    protected @Override Chord<T> setTranspose(int semitones) {
        passOnToChords(x-> x.setTranspose(semitones));
        this.attributes.transpose=semitones;
        return this;
    }

    private void passOnToChords(Consumer<Chord<?>> consumer) {
        if (usingParentAttributes) {
            usingParentAttributes=false;
            attributes=new NoteAttributes(attributes);
            if (subChords!=null)
                for (Chord<?> ch: subChords)
                    if (ch.usingParentAttributes)
                        ch.attributes=attributes;
        }
        if (subChords!=null)
            for (Chord<?> ch: subChords)
                if (!ch.usingParentAttributes)
                    consumer.accept(ch);
    }
}
