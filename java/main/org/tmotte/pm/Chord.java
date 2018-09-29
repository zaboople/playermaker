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
    private final List<Note<T>> notes=new ArrayList<>();//FIXME
    private final List<Integer> pitches=new ArrayList<>();
    private final long restBefore;
    private NoteAttributes attributes;
    private boolean usingParentAttributes=true;
    private List<Bend> bends=null;
    private List<Chord<Chord<T>>> subChords=null;
    private long duration;


    protected Chord(T parent, NoteAttributes attributes, long duration, int... pitches) {
        this(parent, attributes, 0L, duration, pitches);
    }
    private Chord(T parent, NoteAttributes attributes, long restBefore, long duration, int... pitches) {
        this.parent=parent;
        this.attributes=attributes;
        this.restBefore=restBefore;
        this.duration=duration;
        for (int p: pitches) {
            this.pitches.add(p);
            notes.add(new Note<>(this, duration, restBefore, p));
        }
    }

    public long duration() {
        return duration;
    }

    /**
     * Takes us back to the original Player, for the sake of "fluent"
     * programming.
     */
    public T up() {
        return parent;
    }

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

    ///////////
    // RESTS //
    ///////////

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


    ////////////////////////////////////
    //                                //
    // PUBLIC BUT INTERNAL OVERRIDES: //
    //                                //
    ////////////////////////////////////

    /**
     * Mostly for internal use; obtains the total duration of the Chord by our internal (not midi) tick system,
     * not in the general notation used for Chord/Note input durations.
     */
    public @Override long durationForBend() {
        return notes.isEmpty()
            ? duration
            : notes.stream().map(n ->n.restBefore + n.duration)
                .reduce(
                    0L,
                    (x,y)-> y>x ?y :x
                )
            ;
    }

    protected long totalDuration() {
        return notes.isEmpty()
            ?(
                subChords==null
                    ?0
                    :subChords.stream().map(Chord::totalDuration)
                        .reduce(
                            0L,
                            (x,y)-> y>x ?y :x
                        )
            )
            : notes.stream().map(n ->n.restBefore + n.duration)
                .reduce(
                    0L,
                    (x,y)-> y>x ?y :x
                )
            ;
    }


    /**
     * Adds a Chord containing one Note for the specified duration, and returns that Note.
     * Duration and notes work the same as for @link{#p(int, int...)}
     */
    public Note<T> n(int duration, int note) {
        return addNote(Divisions.convert(duration), 0L, note);
    }
    public Note<T> n(double duration, int note) {
        return addNote(Divisions.convert(duration), 0L, note);
    }
    Note<T> addNote(long duration, long restBefore, int pitch) {
        addChord(duration, restBefore, pitch);
        Note<T> n=new Note<>(this, duration, restBefore, pitch);
        notes.add(n);
        return n;
    }


    public Chord<Chord<T>> c(int duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    public Chord<Chord<T>> c(double duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    private Chord<Chord<T>> addChord(long duration, int... pitches) {
        var chord=new Chord<>(this, attributes, duration, pitches);
        subChords.add(chord);
        return chord;
    }


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

    ////////////////
    //            //
    // INTERNALS: //
    //            //
    ////////////////

    List<Integer> pitches() {
        return pitches;
    }
    List<Chord<Chord<T>>> chords() {
        return subChords==null ?Collections.emptyList() :subChords;
    }
    List<Note<T>> notes() {
        return notes;
    }
    List<Bend> bends() {
        return bends==null ?Collections.emptyList() :bends;
    }


    /** Exposed for use by Rest, which will supply a non-zero restBefore */
    Chord<Chord<T>> addChord(long duration, long restBefore, int... pitches) {
        var n=new Chord<>(this, attributes, duration, restBefore, pitches);
        if (subChords==null) subChords=new ArrayList<>();
        subChords.add(n);
        return n;
    }

    private Rest<T> rest(long duration) {
        return new Rest<>(this, duration);
    }

    private Chord<T> t(long duration) {
        this.duration+=duration;
        for (Note n: notes)
            n.t(duration);
        return this;
    }

    /////////////////////////////////////////
    // INTERNAL LOGIC FOR NoteAttributes : //
    /////////////////////////////////////////

    /**
       This is tricky because Notes are created immediately for the
       chord as well as after (via Chord.n()) so we may or may not
       want the notes to inherit attributes. Complicating this is:
       1. We start with the Player's attributes, which we must not change.
          So we have to create a new NoteAttributes if we are still using
          the NoteAttributes we got from Player.
       2. We want Notes to get our changes, but we just made
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
        passOnToNotes(x-> x.setVolume(v));
        passOnToChords(x-> x.setVolume(v));
        this.attributes.volume=v;
        return this;
    }
    protected @Override Chord<T> setTranspose(int semitones) {
        passOnToNotes(x-> x.setTranspose(semitones));
        passOnToChords(x-> x.setTranspose(semitones));
        this.attributes.transpose=semitones;
        return this;
    }
    private void passOnToNotes(Consumer<Note> consumer) {
        NoteAttributes old=attributes;
        if (usingParentAttributes) {
            usingParentAttributes=false;
            attributes=new NoteAttributes(old);
            for (Note note: notes)
                if (note.getNoteAttributesForRead()==old)
                    note.setNoteAttributes(attributes);
        }
        for (Note note: notes)
            if (note.getNoteAttributesForRead()!=this.attributes)
                consumer.accept(note);
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
