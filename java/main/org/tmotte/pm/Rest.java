package org.tmotte.pm;

/**
 * Rest acts as a temporary placeholder, returned by {@link Chord#r(Number)}; the intention is that you
 * want to create a Chord where notes are delayed for an arpeggiated effect.
 *
 * @param <T> Represents the class of the parent Chord; this falls in line
 *   with Chord's pattern of enabling lengthy method chains via same class parameterization.
 * @see Chord#r(Number)
 * @see Rest#c(Number, int...)
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
     * @param duration A duration expressed using the same notation as Player.p(), Chord.c(), etc.
     * @return this
     */
    public Rest<T> t(Number duration) {
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
     * So yes, you can nest chords within others as far as you want to go, e.g.
     * Chord&lt;Chord&lt;Chord...&lt;T&gt;&gt;&gt;.
     * <br>
     * Use .up() to get back to the original chord this Rest was against, or use up(int, int...) as a
     * shortcut instead of .c().
     * <br>
     * Also refer to {@link Chord#c(Number, int...)} if you only want to create parallel chords without
     * rests.
     * @param duration Uses standard notation described in package docs
     * @param notes Notes to play
     * @return Chord instance tied to the Rest's parent Chord
     */
    public Chord<Chord<T>> c(Number duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    private Chord<Chord<T>> addChord(long duration, int... pitches){
        return chord.addChord(restFor, duration, pitches);
    }

    /**
     * Indicates that we should "finish", i.e. play the pitches for the remaining duration of the
     * original Chord.
     * @param pitches The notes to play.
     * @return A new Chord acting as a sub-chord to Rest's parent Chord.
     */
    public Chord<Chord<T>> fin(int... pitches) {
        return addChord(chord.duration()-restFor, pitches);
    }
    /**
     * A shortcut to fin(int...).up()
     * @param pitches The notes to play.
     * @return This Rest's parent Chord.
     */
    public Chord<T> finup(int... pitches) {
        return addChord(chord.duration()-restFor, pitches).up();
    }

    /** A shortcut to c(Number, int...).up() */
    public Chord<T> up(Number duration, int... notes) {
        return c(duration, notes).up();
    }

    /** A shortcut to c(Number, int...).bendWithParent() */
    public Chord<Chord<T>> bwp(Number duration, int... notes) {
        return c(duration, notes).bendWithParent();
    }

}
