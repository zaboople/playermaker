/**
 * Contains all the classes for musical composition and playback. The following is some general-purpose documentation concerning
 * common features.
 * <br>
 * <b>How to specify a note</b>
 *<br>
 * Many methods support a note value as input, specified as an int. In these cases, 0 is C, 1 is C# (or D&#x266d;), and so forth. There
 * are convenience constants for these values in the {@link Pitches} class.
 * <br>
 * When a note accepts a duration as input, these can be expressed as either ints or doubles:
 * <ul>
 *   <li>"8" indicates a 8th note.
 *   <li>"8." (yes, that is valid java syntax) indicates a "dotted" 8th note, which is an 8th + 16th, or 1 &amp; 1/2 times the duration. Java treats
 *       an "8." the same as "8.0", so either is acceptable, but the former is recommended for clarity.
 *   <li>"8.3" indicates a "triplet" 8th note. Whereas two regular 8th notes are the same duration as a quarter note, three eighth notes in a
 *       triplet are also the same duration as a quarter note.
 * </ul>
 */
package org.tmotte.pm2;



