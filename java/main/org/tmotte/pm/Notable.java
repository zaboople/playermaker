package org.tmotte.pm;

/**
 * This is shared by Player, Chord, & Rest.
 * <br>
 * {@link Player}:
 * <ul>
        <li>p() creates a Chord, and returns the Player. Calling p/c/n() again
           will create a new Chord that <em>follows</em> the first. Thus the Chord
           is committed: It cannot be further modified, and additional Chord/Note
           objects will play after it.
        <li>c() creates &amp; returns a Chord that can be further modified.
        <li>n() creates a Chord with one Note, and returns that Note directly. This
            allows detailed modification (vibrato, bend) of only that Note. You can
            still go Note.up() back to the Chord and add more notes, etc.
   </ul>
   {@link  Chord}:
   <ul>
        <li>p() adds notes to the existing chord, then returns Player. The Chord is
            committed and later Chords/Notes will be played after it.
        <li>c() Adds to the existing notes in the Chord,
            thus starting at the same time but optionally with different duration.
        <li>n() Adds a note to the chord, starting at the same time but optionally
            with different duration; and returns that Note allowing for followup
            customization.
    <ul/>
   {@link Rest}:
   <br>
   Acts the same as Chord; all Rest does is add a delay before the Chord/Notes created
   with p/c/n(), so they start at a later time. {@link Rest#fin(int...)} provides a shortcut
   for making the specified notes last as long as the remaining duration of the original Chord.

 */
public interface Notable {


    /**
     * Adds a Chord made of the given notes for the specified duration, and returns the original Player object.
     * @param duration Use 1 for a whole note, 2 for a half note, 4 for a quarter, and so on. For dotted notes
     *       and triplets, use {@link #p(double, int...)}
     * @param notes Follows the 12-tone western scale, with low C at 0, D&#x266d; at 1, and so on, allowing up
     *       as many octaves high as the synthesizer can perform. Note values directly correspond to the midi
     *       standard.
     */
    public default Player p(int duration, int... notes) {
        return c(duration, notes).up();
    }
    /**
     * Adds a Chord made of the given notes for the specified duration, and returns that Chord, which can be further modified.
     * Duration and notes work the same as for @link{#p(int, int...)}
     */
    public default Chord c(int duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    /**
     * Adds a Chord containing one Note for the specified duration, and returns that Note.
     * Duration and notes work the same as for @link{#p(int, int...)}
     */
    public default Note n(int duration, int note) {
        return addNote(Divisions.convert(duration), note);
    }

    /**
     * An alternate version of {@link #p(int, int...)} that accepts a double, allowing
     * dotted and triplet notes, e.g. "8." and "8.3" as respective examples.
     */
    public default Player p(double duration, int... notes) {
        return c(duration, notes).up();
    }
    public default Chord c(double duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    public default Note n(double duration, int note) {
        return addNote(Divisions.convert(duration), note);
    }


    /** Internal use */
    Note addNote(long duration, int note);
    /** Internal use */
    Chord addChord(long duration, int... notes);

    ////////////////////////////////////////////
    // Old way of adding notes... not all bad //
    // but kind of repetitive to maintain:    //
    ////////////////////////////////////////////

    public default Player p1(int... notes) {
        return c(1, notes).up();
    }
    public default Player p2(int... notes) {
        return c(2, notes).up();
    }
    public default Player p4(int... notes) {
        return c(4, notes).up();
    }
    public default Player p8(int... notes) {
        return c(8, notes).up();
    }
    public default Player p16(int... notes) {
        return c(16, notes).up();
    }
    public default Player p32(int... notes) {
        return c(32, notes).up();
    }
    public default Player p64(int... notes) {
        return c(64, notes).up();
    }


}
