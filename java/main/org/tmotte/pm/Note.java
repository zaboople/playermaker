package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Note extends NoteAttributeHolder<Note> implements BendContainer<Note> {
    final Chord chord;
    private List<Bend> bends=null;
    private NoteAttributes attributes;

    final long restBefore;
    final int pitch;
    long duration;


    protected Note(Chord chord, long duration, long restBefore, int pitch) {
        this.chord=chord;
        this.restBefore=restBefore;
        this.pitch=pitch;
        this.duration=duration;
        this.attributes=chord.getNoteAttributesForRead();
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
        return this.chord;
    }
    public Player upup() {
        return this.chord.up();
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

    /////////////////////////////////////////
    // INTERNAL LOGIC FOR NoteAttributes : //
    /////////////////////////////////////////

    protected @Override Note setVolume(int v) {
        getNoteAttributesForWrite().volume=v;
        return this;
    }
    protected @Override Note setTranspose(int semitones) {
        getNoteAttributesForWrite().transpose=semitones;
        return this;
    }
    protected @Override NoteAttributes getNoteAttributesForRead(){
        return attributes;
    }
    /**
     * Used by Chord to pass along an NoteAttributes change
     * if we haven't customized.
     */
    protected void setNoteAttributes(NoteAttributes a) {
        this.attributes=a;
    }
    private NoteAttributes getNoteAttributesForWrite(){
        return attributes==chord.getNoteAttributesForRead()
            ?attributes=new NoteAttributes(attributes)
            :attributes;
    }

}