package org.tmotte.pm;

/**
 * Rest acts as a temporary placeholder, only used by Chord (not Player); the intention is that you
 * want to create a chord where notes are delayed for an arpeggiated effect.
 *
 * @see Player#r(int)
 */
public class Rest implements Notable {
    private final Chord chord;
    private long restFor;

    protected Rest(Chord chord, long restFor) {
        this.chord=chord;
        this.restFor=restFor;
    }

    /**
     * Indicates that we should "finish", i.e. play the pitches for the remaining duration of the original Chord.
     * @return The original Chord.
     */
    public Chord fin(int... pitches) {
        return addChord(chord.totalDuration()-restFor, pitches);
    }

    /**
     * Ties this Rest to another - actually returns itself after extending its duration.
     * @param duration A period expressed using the same notation as Player.p(), Chord.c(), etc.
     */
    public Rest t(int duration) {
        return t(Divisions.convert(duration));
    }
    /** A double version of t(int) for use with dotted & triplet notes.*/
    public Rest t(double duration) {
        return t(Divisions.convert(duration));
    }

    private Rest t(long duration) {
        restFor+=duration;
        return this;
    }


    ////////////////
    // INTERNALS: //
    ////////////////

    /** For internal use, required by Notable */
    public @Override Note addNote(long duration, int pitch) {
        return chord.addNote(duration, restFor, pitch);
    }

    /** For internal use, required by Notable */
    public @Override Chord addChord(long duration, int... pitches){
        for (int n: pitches)
            addNote(duration, n);
        return chord;
    }

}