package org.tmotte.pm;

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
     * Ties this Rest to another - actually returns itself after extending its duration.
     * @param duration A period expressed using the same notation as Player.p(), Chord.c(), etc.
     */
    public Rest<T> t(int duration) {
        return t(Divisions.convert(duration));
    }
    /** A double version of t(int) for use with dotted &amp; triplet notes.*/
    public Rest<T> t(double duration) {
        return t(Divisions.convert(duration));
    }
    private Rest<T> t(long duration) {
        restFor+=duration;
        return this;
    }


    /**
     * Does the same as Player.c(), but in this case a "sub-chord" is returned, since the original Rest
     * was against a Chord to begin with. The new chord will play after the start of the original chord
     * but (usually) before the end of the same original chord.
     * <br>
     * You can use .bend(), .vibrato(), .volume() and even .r() against the new Chord like any other.
     * So yes, you can nest chords within others as far as you want to go, e.g. Chord&lt;Chord&lt;Chord...&lt;T&gt;&gt;&gt;.
     * <br>
     * Use .up() to get back to the parent chord, or use up(int, int...) as a shortcut instead of .c().
     */
    public Chord<Chord<T>> c(int duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    public Chord<Chord<T>> c(double duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    private Chord<Chord<T>> addChord(long duration, int... pitches){
        return chord.addChord(restFor, duration, pitches);
    }

    /**
     * Indicates that we should "finish", i.e. play the pitches for the remaining duration of the original Chord.
     * @return The original Chord.
     */
    public Chord<Chord<T>> fin(int... pitches) {
        return addChord(chord.duration()-restFor, pitches);
    }
    /**
     * A shortcut to fin(int...).up()
     */
    public Chord<T> finup(int... pitches) {
        return addChord(chord.duration()-restFor, pitches).up();
    }
    /**
     * A shortcut to fin(int...).bendWithParent()
     */
    public Chord<Chord<T>> finb(int... pitches) {
        return addChord(chord.duration()-restFor, pitches).bendWithParent();
    }


    /** A shortcut to c(int, int...).up() */
    public Chord<T> up(int duration, int... notes) {
        return c(duration, notes).up();
    }
    /** A shortcut to c(double, int...).up() */
    public Chord<T> up(double duration, int... notes) {
        return c(duration, notes).up();
    }

    /** A shortcut to c(int, int...).bendWithParent() */
    public Chord<Chord<T>> bwp(int duration, int... notes) {
        return c(duration, notes).bendWithParent();
    }
    /** A shortcut to c(double, int...).bendWithParent() */
    public Chord<Chord<T>> bwp(double duration, int... notes) {
        return c(duration, notes).bendWithParent();
    }


}