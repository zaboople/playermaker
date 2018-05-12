package org.tmotte.pm;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;


public class Note {

    final long restBefore;
    final long duration;
    final int pitch;
    final Sound sound;
    final NoteAttributes attrs;
    private List<Bend> bends=null;
    private List<Vibrato> vibratos=null;

    public Note(Sound sound, long duration, long restBefore, int pitch) {
        this.sound=sound;
        this.attrs=new NoteAttributes(sound.attrs());
        this.duration=duration;
        this.restBefore=restBefore;
        this.pitch=pitch;
    }

    public Sound up() {
        return this.sound;
    }
    public Player upup() {
        return this.sound.up();
    }

    public NoteAttributes attrs() {
        return this.attrs;
    }
    class NoteAttributes extends TonalAttributes {
        public NoteAttributes(TonalAttributes other) {
            super(other);
        }
        public Note up() {
            return Note.this;
        }
    }

    public List<Bend> bends() {
        return bends==null ?Collections.emptyList() :bends;
    }
    public Note bend(long duration, int semitones) {
        return bend(0, duration, semitones);
    }
    public Note bend(double duration, int semitones) {
        return bend(0D, duration, semitones);
    }
    public Note bend(long delay, long duration, int semitones) {
        bends=Bend.add(bends, delay, duration, semitones);
        return this;
    }
    public Note bend(double delay, double duration, int semitones) {
        bends=Bend.add(bends, delay, duration, semitones);
        return this;
    }


    public Note vibrato(long frequency, int denominator) {
        //FIXME this may need a durationShortForm() treatment
        return vibrato(0, duration, frequency, denominator);
    }
    public Note vibrato(double frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Note vibrato(long duration, long frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Note vibrato(double duration, double frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Note vibrato(long delay, long duration, long frequency, int denominator) {
        long count=frequency/duration;
        System.out.println(count);
        int flipper=1;
        for (long i=0; i<count; i++) {
            bend((i==0 && delay>0 ?delay :0), frequency, denominator * flipper);
            flipper*=-1;
        }
        return this;
    }
    public Note vibrato(double delay, double duration, double frequency, int denominator) {
        //FIXME
        //vibratos=Vibrato.add(vibratos, delay, duration, frequency, denominator);
        return this;
    }

}