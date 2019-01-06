/**
 * Contains all of PlayerMaker's end-user classes for musical composition and playback. The following is some general-purpose
 * documentation concerning common features.
 * <br>
 *
 * <h3>Notation for pitch &amp; duration</h3>
 * Many methods support a note value as input, specified as an int. In these cases, 0 is C, 1 is C# (or D&#x266d;), and so forth. There
 * are convenience constants for these values in the {@link Pitches} class.
 * <br>
 * These methods also accept a duration as input, which can be expressed as a java Number. You will typically take advantage of "autoboxing",
 * expressing the Number as a double or int:
 * <ul>
 *   <li>"8" indicates a 8th note.
 *   <li>"8." (yes, that is valid java syntax) indicates a "dotted" 8th note, which is an 8th + 16th, i.e. 1 &amp; 1/2 times the duration. Java treats
 *       an "8." the same as "8.0", so either is acceptable, but the former is recommended for clarity.
 *   <li>"8.3" indicates a "triplet" 8th note. Whereas two regular 8th notes are the same duration as a quarter note, three eighth notes in a
 *       triplet are also the same duration as a quarter note.
 * </ul>
 * Be careful about using the <code>long</code> type for durations: Internally, your <code>double</code> &amp; <code>int</code>
 * "symbolic" values are converted into <code>long</code>s that represent the exact number of ticks in PlayerMaker's timing system.
 * So an "8" or "8.0" or "8." will be converted, but a "8L" will not.
 * <p>
 * However: Suppose you want to combine the following into a single duration:
 * <br>
 *   quarter note + eighth note + sixteenth note + thirty-second note
 * <br>
 * One way (and sometimes the only way, with bends &amp; swells) is the static {@link Chord#tie(Number...)}:
   <pre>

   Chord.tie(4, 8, 16, 32)
   </pre>
 * This will return a long value which will be used as is, no further conversion necessary. You can of course mix and match
 * doubles and ints with Chord.tie() as you please, since it accepts a varargs of Number. You might also find it useful to
 * do <code>import static Chord.tie;</code> for brevity.
 *
 * <h3>Bends &amp; Vibrato:</h3>
 * Note "bends" can be done using {@link Chord#bend(Number, Number, int)} and other variations of Chord.bend().
 * There is also {@link Chord#vibrato(Number, Number, Number, int)}, which can be used instead of {@link Player#pressure(int)}
 * for more control over vibrato.
 * <p>
 * Bends &amp; vibratos are done sequentially, so you can do
     <pre>
     chord.bend(...).bend(....)..vibrato(...).bend(...)
     </pre>
   and each bend or vibrato will happen after the previous. So, no, you can't do vibrato &amp; bend at the same
   time.
 * <p>
 * In Midi, bends apply to the whole channel. This is rather inflexible, so internally we make use of "spare"
 * channels when bends are applied differently to simultaneous notes, or if only certain notes in a Chord are bent.
 * This is handled invisibly, except that when using multiple Players and bending notes, you should assign the
 * Players channels with gaps in between, e.g. for three Players you might assign 0, 3, 6 instead of 0, 1, 2.
 * This would give the first two players two extra channels, and the 3rd player all of the rest (except channel 10,
 * which can only play drums). Refer to {@link Player#Player(int)}.
 */

package org.tmotte.pm;



