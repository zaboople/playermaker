package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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


}