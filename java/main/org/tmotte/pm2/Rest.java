package org.tmotte.pm2;

/**
 * Rest acts as a temporary placeholder, only used by Chord (not Player); the intention is that you
 * want to create a chord where notes are delayed for an arpeggiated effect.
 *
 * @see Player#r(int)
 */
public class Rest<T> {
    private final Chord<T> chord;
    private long restFor;

    protected Rest(Chord<T> chord, long restFor) {
        this.chord=chord;
        this.restFor=restFor;
    }

    /**
     * Indicates that we should "finish", i.e. play the pitches for the remaining duration of the original Chord.
     * @return The original Chord.
     */
    public Chord<Chord<T>> fin(int... pitches) {
        return addChord(chord.duration()-restFor, pitches);
    }

    public Chord<Chord<T>> c(int duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    public Chord<Chord<T>> c(double duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    private Chord<Chord<T>> addChord(long duration, int... pitches){
        return chord.addChord(restFor, duration, pitches);
    }

    public Chord<T> up(int duration, int... notes) {
        return addChord(Divisions.convert(duration), notes).up();
    }
    public Chord<T> up(double duration, int... notes) {
        return addChord(Divisions.convert(duration), notes).up();
    }


    /**
     * Ties this Rest to another - actually returns itself after extending its duration.
     * @param duration A period expressed using the same notation as Player.p(), Chord.c(), etc.
     */
    public Rest<T> t(int duration) {
        return t(Divisions.convert(duration));
    }
    /** A double version of t(int) for use with dotted & triplet notes.*/
    public Rest<T> t(double duration) {
        return t(Divisions.convert(duration));
    }
    private Rest<T> t(long duration) {
        restFor+=duration;
        return this;
    }



}