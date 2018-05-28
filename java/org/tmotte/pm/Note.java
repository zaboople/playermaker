package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Note implements BendContainer<Note> {
    long duration;
    private List<Bend> bends=null;

    final long restBefore;
    final int pitch;
    final Chord sound;
    final NoteAttributes attrs;

    public Note(Chord sound, long duration, long restBefore, int pitch) {
        this.sound=sound;
        this.attrs=new NoteAttributes(sound.attrs());
        this.duration=duration;
        this.restBefore=restBefore;
        this.pitch=pitch;
    }

    public Note t(long duration) {
        this.duration+=duration;
        return this;
    }
    public Note t(int duration) {
        return t(Divisions.convert(duration));
    }
    public Note t(double duration) {
        return t(Divisions.convert(duration));
    }

    public Chord up() {
        return this.sound;
    }
    public Player upup() {
        return this.sound.up();
    }

    /** For internal use */
    public @Override List<Bend> makeBends() {
        if (bends==null)
            bends=new ArrayList<>();
        return bends;
    }
    /** For internal use */
    public @Override Note self(){
        return this;
    }
    /** For internal use */
    public @Override long totalDuration(){
        return duration;
    }

    NoteAttributes attrs() {
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

    List<Bend> bends() {
        return bends==null ?Collections.emptyList() :bends;
    }



}