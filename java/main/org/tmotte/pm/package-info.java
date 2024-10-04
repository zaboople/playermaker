/**
 * This package contains all of PlayerMaker's end-user classes for musical composition and playback. The following
 * is some general-purpose documentation concerning common features.
 * <h2>Entry point(s)</h2>
 * The two main classes you will start with are {@link Player}, which contains compositional data, and {@link MyMidi3},
 * which sequences the midi data and plays it through a synthesizer.
 * <h2>Notation for pitch &amp; duration</h2>
 * Many methods support a note value as input, specified as an int. In these cases, 0 is C, 1 is C# (or D&#x266d;), and
 * so forth. Thereare convenience constants for these values in the {@link Pitches} class.
 * <br>
 * These methods also accept a duration as input, which can be expressed as a java Number. You will typically take
 * advantage of "autoboxing", expressing the Number as a double or int:
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
 * Suppose you want to combine the following into a single duration:
 * <br>
 *   &nbsp;&nbsp;<code>quarter note + eighth note + sixteenth note + thirty-second note</code>
 * <br>
 * There are two ways:
 * <ol>
 *   <li> Use {@link Tie#tie(Number...)}:
 *   This will return a Tie, which extends Number. You can of course mix and match
 *   doubles and ints with Tie.tie() as you please, since it accepts a varargs of Number. You might also find it useful to
 *   do <code>import static Tie.tie;</code> for brevity.
 *   <li> Use {@link Chord#t(Number)}: The "t" is short for "tie". This shortcut will extend the duration of the Chord by
 *   the given duration. This is less flexible than option #1, but it's there if you want to use it.
 * </ol>
 * Note that you can use negative numbers when using ties! This allows you to subtract a certain amount, e.g. Chord.t(4, -32)
 * would reduce the quarter note duration by a 32nd note.
 *
 * <h2>Bends &amp; Vibrato:</h2>
 * Note "bends" can be done using {@link Chord#bend(Number, Number, int)} and other variations of Chord.bend().
 * There is also {@link Chord#vibrato(Number, Number, Number, int)}, which can be used instead of {@link Player#pressure(int)}
 * for more control over vibrato.
 * <p>
 * Bends &amp; vibratos are done sequentially, so you can do
     <pre>
     chord.bend(...).bend(....)..vibrato(...).bend(...)
     </pre>
   and each bend or vibrato will happen after the previous. So, no, you can't do vibrato() &amp; bend() at the same
   time, although vibratos are really just a rapid series of bends.
 * <p>
 * In Midi, bends apply to the whole channel. This is rather inflexible, so internally we make use of "spare"
 * channels when bends are applied differently to simultaneous notes, or if only certain notes in a Chord are bent.
 * This is handled invisibly, except that when using multiple Players and bending notes, you should assign the
 * Players channels with gaps in between, e.g. for three Players you might assign 0, 3, 6 instead of 0, 1, 2.
 * This would give the first two players two extra channels, and the 3rd player all of the rest (except channel 10,
 * which can only play drums). Refer to {@link Player#Player(int)}.
 */
package org.tmotte.pm;



