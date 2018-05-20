package org.tmotte.pm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @param delay A period to wait before the bend; this can be expressed as
 *        2/4/8/16/32/64 etc to indicate a period corresponding to half/quarter/eighth/etc
 *        notes (for triplet and dotted note delays, use Bend(double, double, int)).
 * @param denominator Can be negative or positive. Indicates the 1/denominator of our bend range to go
 * up or down. So, if our bend sensitivity is set to the default of one whole step:
   <ul>
     <li>1 is a whole step, e.g. C to D
     <li>2 is a half step, e.g. C to C#
     <li>4 is a quarter step (obviously off key but it's jazzy that way)
   </ul>
 * And so forth.
 */
public class Sound extends AbstractSound<Sound> implements BendContainer {
    private Player player;
    private List<Note> notes=new ArrayList<>();
    private List<Bend> bends=null;
    int instrument;

    Sound(Player player, long duration, int... pitches) {
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
    long totalDuration() {
        return
            notes.stream().map(n ->
                n.restBefore + n.duration
            ).reduce(
                0L,
                (x,y)-> y>x ?y :x
            );
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

    // BEND: //

    public List<Bend> bends() {
        return bends==null ?Collections.emptyList() :bends;
    }
    public @Override void setBends(List<Bend> bends) {
        this.bends=bends;
    }
    public @Override List<Bend> getBends() {
        return bends;
    }

    public Sound bend(int denominator) {
        return bend(0L, totalDuration(), denominator);
    }

    public Sound bend(long duration, int denominator) {
        return bend(0L, duration, denominator);
    }
    public Sound bend(long delay, long duration, int denominator) {
        return Bend.add(this, delay, duration, denominator);
    }

    public Sound bend(double duration, int denominator) {
        return bend(0D, duration, denominator);
    }
    public Sound bend(double delay, double duration, int denominator) {
        return Bend.add(this, delay, duration, denominator);
    }

    public Sound bend(int duration, int denominator) {
        return bend(0, duration, denominator);
    }
    public Sound bend(int delay, int duration, int denominator) {
       return Bend.add(this, delay, duration, denominator);
    }

    // VIBRATO: //

    public Sound vibrato(int frequency, int denominator) {
        return vibrato(0L, totalDuration(), Divisions.convert(frequency), denominator);
    }
    public Sound vibrato(int duration, int frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Sound vibrato(int delay, int duration, int frequency, int denominator) {
        return Bend.vibrato(this, delay, duration, frequency, denominator);
    }

    public Sound vibrato(long frequency, int denominator) {
        return vibrato(0, totalDuration(), frequency, denominator);
    }
    public Sound vibrato(long duration, long frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Sound vibrato(long delay, long duration, long frequency, int denominator) {
        return Bend.vibrato(this, delay, duration, frequency, denominator);
    }

    public Sound vibrato(double frequency, int denominator) {
        return vibrato(0L, totalDuration(), Divisions.convert(frequency), denominator);
    }
    public Sound vibrato(double duration, double frequency, int denominator) {
        return vibrato(0D, duration, frequency, denominator);
    }
    public Sound vibrato(double delay, double duration, double frequency, int denominator) {
        return Bend.vibrato(this, delay, duration, frequency, denominator);
    }

    ////////////////
    // INTERNALS: //
    ////////////////

    protected @Override Sound addSound(long duration, int... pitches) {
        for (int p: pitches)
            addNote(duration, p);
        return this;
    }
    protected @Override Note addNote(long duration, int pitch) {
        return addNote(duration, 0, pitch);
    }
    protected @Override Sound self() {
        return this;
    }

    protected Note addNote(long duration, long restBefore, int pitch) {
        Note n=new Note(this, duration, restBefore, pitch);
        notes.add(n);
        return n;
    }

    private Rest rest(long duration) {
        return new Rest(this, duration);
    }

}
