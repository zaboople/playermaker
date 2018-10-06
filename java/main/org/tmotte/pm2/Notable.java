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
public interface Notable<T> {






}
