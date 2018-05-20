package org.tmotte.pm;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;


public class Note implements BendContainer {

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
    public @Override void setBends(List<Bend> bends) {
        this.bends=bends;
    }
    public @Override List<Bend> getBends() {
        return bends;
    }

    public Note bend(int denominator) {
        return bend(0L, duration, denominator);
    }

    public Note bend(long duration, int denominator) {
        return bend(0L, duration, denominator);
    }
    public Note bend(long delay, long duration, int denominator) {
        return Bend.add(this, delay, duration, denominator);
    }

    public Note bend(double duration, int denominator) {
        return bend(0D, duration, denominator);
    }
    public Note bend(double delay, double duration, int denominator) {
        return Bend.add(this, delay, duration, denominator);
    }

    public Note bend(int duration, int denominator) {
        return bend(0, duration, denominator);
    }
    public Note bend(int delay, int duration, int denominator) {
        System.out.println("Note.bend() 3 int");
        return Bend.add(this, delay, duration, denominator);
    }


    public Note vibrato(int frequency, int denominator) {
        return vibrato(0L, duration, Divisions.convert(frequency), denominator);
    }
    public Note vibrato(int duration, int frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Note vibrato(int delay, int duration, int frequency, int denominator) {
        return Bend.vibrato(this, 0, duration, frequency, denominator);
    }

    public Note vibrato(long frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Note vibrato(long duration, long frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public Note vibrato(long delay, long duration, long frequency, int denominator) {
        return Bend.vibrato(this, delay, duration, frequency, denominator);
    }

    public Note vibrato(double frequency, int denominator) {
        return vibrato(0L, duration, Divisions.convert(frequency), denominator);
    }
    public Note vibrato(double duration, double frequency, int denominator) {
        return vibrato(0D, duration, frequency, denominator);
    }
    public Note vibrato(double delay, double duration, double frequency, int denominator) {
        return Bend.vibrato(this, delay, duration, frequency, denominator);
    }

}