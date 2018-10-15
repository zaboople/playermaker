/**
 * Contains all the classes for musical composition and playback. The following is some general-purpose documentation concerning
 * common features.
 * <br>
 * <b>How to specify a note</b>
 *<br>
 * Many methods support a note value as input, specified as an int. In these cases, 0 is C, 1 is C# (or D&#x266d;), and so forth. There
 * are convenience constants for these values in the {@link Pitches} class.
 * <br>
 * These methods also accept a duration as input, which can be expressed as either int or double:
 * <ul>
 *   <li>"8" indicates a 8th note.
 *   <li>"8." (yes, that is valid java syntax) indicates a "dotted" 8th note, which is an 8th + 16th, or 1 &amp; 1/2 times the duration. Java treats
 *       an "8." the same as "8.0", so either is acceptable, but the former is recommended for clarity.
 *   <li>"8.3" indicates a "triplet" 8th note. Whereas two regular 8th notes are the same duration as a quarter note, three eighth notes in a
 *       triplet are also the same duration as a quarter note.
 * </ul>
 * <br>
 * <b>Bends &amp; Vibrato</b>
 * Note "bends" can be done using {@link Chord#bend(Number, Number, int) and other variations of Chord.bend().
 * There is also {@link Chord#vibrato(Number, Number, Number, int), which can be used instead of {@link Player.pressure(int)}
 * for more control over vibrato.
 * <br>
 * Bends & vibratos are done sequentially, so you can do
     <pre>
     chord.bend(...).bend(....)..vibrato(...).bend(...)
     </pre>
   and each bend or vibrato will happen after the previous. So, no, you can't do vibrato & bend at the same
   time.
 * <br>
 * In Midi, bends apply to the whole channel. This is rather inflexible, so internally we make use of "spare"
 * channels when bends are applied differently to simultaneous notes, or if only certain notes in a Chord are bent.
 * This is handled invisibly, except that when using multiple Players and bending notes, you should assign the
 * Players channels with gaps in between, e.g. for three Players you might assign 0, 3, 6 instead of 0, 1, 2.
 * This would give the first two players two extra channels, and the 3rd player all of the rest (except channel 10,
 * which can only play drums). Refer to {@link Player#channel(int)}.
 */

package org.tmotte.pm;



