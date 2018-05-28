package org.tmotte.pm;

/**
 * Rest acts as a temporary placeholder, only used by Chord (not Player); the intention is that you
 * want to create a chord where notes are delayed for an arpeggiated effect.
 */
public class Rest implements Notable {
    final Chord sound;
    final long restFor;

    public Rest(Chord sound, long restFor) {
        this.sound=sound;
        this.restFor=restFor;
    }

    /** For internal use, required by Notable */
    public @Override Note addNote(long duration, int pitch) {
        return sound.addNote(duration, restFor, pitch);
    }

    /** For internal use, required by Notable */
    public @Override Chord addChord(long duration, int... pitches){
        for (int n: pitches)
            addNote(duration, n);
        return sound;
    }

}