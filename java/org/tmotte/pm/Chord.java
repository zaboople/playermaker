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
 *
 */
public class Chord extends NoteAttributeHolder<Chord> implements BendContainer<Chord>, Notable {
    private final Player player;
    private NoteAttributes attributes;
    private List<Note> notes=new ArrayList<>();
    private List<Bend> bends=null;

    protected Chord(Player player, long duration, int... pitches) {
        this.player=player;
        this.attributes=player.getNoteAttributesForRead();
        addChord(duration, pitches);
    }

    public Player up() {
        return player;
    }

    public Chord t(long duration) {
        for (Note n: notes)
            n.t(duration);
        return this;
    }
    public Chord t(int duration) {
        return t(Divisions.convert(duration));
    }
    public Chord t(double duration) {
        return t(Divisions.convert(duration));
    }


    public Rest r(int i) {return rest(Divisions.convert(i));}
    public Rest r(double d) {return rest(Divisions.convert(d));}

    public Rest r1() {return rest(Divisions.reg2);}
    public Rest r2() {return rest(Divisions.reg2);}
    public Rest r4() {return rest(Divisions.reg4);}
    public Rest r8() {return rest(Divisions.reg8);}
    public Rest r16() {return rest(Divisions.reg16);}
    public Rest r32() {return rest(Divisions.reg32);}
    public Rest r64() {return rest(Divisions.reg64);}

    public Rest r8_3() {return rest(Divisions.triplet8);}
    public Rest r16_3() {return rest(Divisions.triplet16);}
    public Rest r32_3() {return rest(Divisions.triplet32);}
    public Rest r64_3() {return rest(Divisions.triplet64);}

    List<Note> notes() {
        return notes;
    }
    List<Bend> bends() {
        return bends==null ?Collections.emptyList() :bends;
    }

    ////////////////
    // INTERNALS: //
    ////////////////

    /** For internal use by BendContainer (from which we override) and MyMidi3 and ok other things....*/
    public @Override long totalDuration() {
        return
            notes.stream().map(n ->
                n.restBefore + n.duration
            ).reduce(
                0L,
                (x,y)-> y>x ?y :x
            );
    }

    /** For internal use, required by Notable */
    public @Override Chord addChord(long duration, int... pitches) {
        for (int p: pitches)
            addNote(duration, p);
        return this;
    }

    /** For internal use, required by Notable */
    public @Override Note addNote(long duration, int pitch) {
        return addNote(duration, 0, pitch);
    }

    /** For internal use, required by BendContainer */
    public @Override Chord self() {
        return this;
    }

    /** For internal use, required by BendContainer */
    public @Override List<Bend> makeBends() {
        if (bends==null)
            bends=new ArrayList<>();
        return bends;
    }

    /** Exposed for use by Rest, which will supply a non-zero restBefore */
    Note addNote(long duration, long restBefore, int pitch) {
        Note n=new Note(this, duration, restBefore, pitch);
        notes.add(n);
        return n;
    }

    private Rest rest(long duration) {
        return new Rest(this, duration);
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
    protected @Override Chord setVolume(int v) {
        passOnToNotes(note-> note.setVolume(v));
        this.attributes.volume=v;
        return this;
    }
    protected @Override Chord setTranspose(int semitones) {
        passOnToNotes(note-> note.setTranspose(semitones));
        this.attributes.transpose=semitones;
        return this;
    }
    private void passOnToNotes(Consumer<Note> consumer) {
        NoteAttributes old=attributes;
        if (attributes==player.getNoteAttributesForRead()) {
            attributes=new NoteAttributes(old);
            for (Note note: notes)
                if (note.getNoteAttributesForRead()==old)
                    note.setNoteAttributes(attributes);
        }
        for (Note note: notes)
            if (note.getNoteAttributesForRead()!=this.attributes)
                consumer.accept(note);
    }
}
