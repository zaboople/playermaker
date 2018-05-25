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
 * FIXME change name to Chord.
 *
 */
public class Sound extends AttributeHolder<Sound> implements BendContainer<Sound>, Notable {
    private Player player;
    private List<Note> notes=new ArrayList<>();
    private List<Bend> bends=null;
    int instrument;

    protected Sound(Player player, long duration, int... pitches) {
        super(new TonalAttributes(player.attrs()));
        this.player=player;
        this.instrument=player.instrumentIndex;
        addSound(duration, pitches);
    }

    public List<Note> notes() {
        return notes;
    }
    public Player up() {
        return player;
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

    /////////////////////
    // BEND & VIBRATO: //
    /////////////////////

    public List<Bend> bends() {
        return bends==null ?Collections.emptyList() :bends;
    }
    public @Override void setBends(List<Bend> bends) {
        this.bends=bends;
    }
    public @Override List<Bend> getBends() {
        return bends;
    }
    /** Used by BendContainer (from which we override) and MyMidi3 */
    public @Override long totalDuration() {
        return
            notes.stream().map(n ->
                n.restBefore + n.duration
            ).reduce(
                0L,
                (x,y)-> y>x ?y :x
            );
    }


    ////////////////
    // INTERNALS: //
    ////////////////

    /** For internal use, required by Notable */
    public @Override Sound addSound(long duration, int... pitches) {
        for (int p: pitches)
            addNote(duration, p);
        return this;
    }

    /** For internal use, required by Notable */
    public @Override Note addNote(long duration, int pitch) {
        return addNote(duration, 0, pitch);
    }

    /** For internal use, required by BendContainer &amp; AttributeHolder */
    public @Override Sound self() {
        return this;
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
