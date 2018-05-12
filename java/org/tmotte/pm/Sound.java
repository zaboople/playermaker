package org.tmotte.pm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Sound extends AbstractSound<Sound> {
    private Player player;
    private List<Note> notes=new ArrayList<>();
    private List<Bend> bends=null;

    Sound(Player player, long duration, int... pitches) {
        super(new TonalAttributes(player.attrs()));
        this.player=player;
        addSound(duration, pitches);
    }

    public List<Note> notes() {
        return notes;
    }
    public Player up() {
        return player;
    }
    long totalDuration() {
        return reduce(notes.stream().map(n -> n.restBefore + n.duration));
    }
    private long durationShortForm() {
        return reduce(
            notes.stream().map(
                // Strangely, conversion is done the same backwards & forwards:
                n -> Divisions.convert(n.restBefore) + Divisions.convert(n.duration)
            )
        );
    }
    private long reduce(Stream<Long> lengths) {
        return lengths.reduce(
            0L,
            (x,y)-> y>x ?y :x
        );
    }


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



    public List<Bend> bends() {
        return bends==null ?Collections.emptyList() :bends;
    }
    public Sound bend(long duration, int denominator) {
        return bend(0, duration, denominator);
    }
    public Sound bend(double duration, int denominator) {
        return bend(0D, duration, denominator);
    }
    public Sound bend(long delay, long duration, int denominator) {
        bends=Bend.add(bends, delay, duration, denominator);
        return this;
    }
    public Sound bend(double delay, double duration, int denominator) {
        bends=Bend.add(bends, delay, duration, denominator);
        return this;
    }

    public Sound vibrato(long frequency, int denominator) {
        // FIXME If necessary we can adapt Bend() to handle long form; it already
        // does a conversion internally. We'd just need to convert
        // frequency as well, which is kind of a good thing
        return vibrato(0, durationShortForm(), frequency, denominator);
    }
    public Sound vibrato(double frequency, int denominator) {
        return vibrato(0, (double) durationShortForm(), frequency, denominator);
    }
    public Sound vibrato(long duration, long frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Sound vibrato(double duration, double frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    /**
     * @param denominator Must be divisible by 2
     */
    public Sound vibrato(long delay, long duration, long frequency, int denominator) {
        if (denominator % 2 != 0)
            throw new RuntimeException("Denominator should be divisible by 2; value was "+denominator);
        // Oh this is so weird: We divide frequency by duration because
        long count=frequency/duration;
        int flipper=1;
        for (long i=0; i<count; i++) {
            bend(delay, frequency, denominator * (flipper*=-1));
            if (i==0) {
                delay=0;
                denominator/=2;
            }
        }
        return this;
    }
    public Sound vibrato(double delay, double duration, double frequency, int denominator) {
        //FIXME
        //vibratos=Vibrato.add(vibratos, delay, duration, frequency, denominator);
        return this;
    }

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
