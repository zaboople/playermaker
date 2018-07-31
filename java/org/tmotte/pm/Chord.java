package org.tmotte.pm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a chord, be it one note or many. While a chord will often sound all of its
 * notes at once and finish them all at once, overlapping/arpeggiating possibilities are
 * allowed via the r() method, which creates a Rest, and then delayed notes can be added.
 * FIXME test much overlapping waxing/waning etc.
 *
 */
public class Chord extends AttributeHolder<Chord> implements BendContainer<Chord>, Notable {
    private Player player;
    private List<Note> notes=new ArrayList<>();
    private List<Bend> bends=null;

    protected Chord(Player player, long duration, int... pitches) {
        super(player);
        this.player=player;
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

    /** For internal use, required by BendContainer &amp; AttributeHolder */
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

}
