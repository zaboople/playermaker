package org.tmotte.pm;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;


public class Note implements BendContainer<Note> {

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
    public @Override Note self(){
        return this;
    }
    public @Override long totalDuration(){
        return duration;
    }


}