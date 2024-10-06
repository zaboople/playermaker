package org.tmotte.pm;

/**
 * This is extended by Chord and Player. Player passes its attributes on to Chord when creating a Chord
 * via {@link Player#c(Number, int...)}, but Chord can override those settings. Chord passes its own attributes
 * on to its sub-chords similarly.
 * <br>
 * The only actual things set in here are octave/transpose &amp; volume.
 * @param <T> Represents the class extending this one, allowing for method chaining used by Player &amp; Chord.
 */
public abstract class NoteAttributeHolder<T> {
    protected NoteAttributeHolder() {}

    /**
     * Sets an absolute value for volume, directly corresponding to the midi standard volume settings.
     * @param v Should be between 0 &amp; 127 inclusive.
     * @return The orginal object
     */
    public T volume(int v) {
        return setVolume(v);
    }
    /**
     * Adds the given amount to the current volume setting; both positive &amp; negative values
     * are allowed as long as the resulting value fits within standard limits.
     * @param change The amount to add. Resulting volume should follow the limits for volume(int).
     * @return this, with child type preserved
     */
    public T addVolume(int change) {
        return volume(getNoteAttributesForRead().volume+change);
    }
    /** Gets the current volume setting
    @return volume */
    public int volume() {
        return getNoteAttributesForRead().volume;
    }


    /**
     * Set the octave
     * @param octave 0 for the default octave, or any positive number to modulate up from 0 by
     *   that many octaves.
     * @return this, preserving type
     */
    public T octave(int octave) {
        return setTranspose(octave*12);
    }
    /**
     * Modulates by individual semitones, not octaves; adds to the current transposition
     * setting instead of treating semitones as an absolute value. Use octave() to reset
     * to a normal octave.
     * @param semitones The number of semitones to modulate by
     * @return this, preserving type
     */
    public T modulate(int semitones) {
        return setTranspose(
            getNoteAttributesForRead().transpose+semitones
        );
    }
    /** Obtains the transposition value set by either octave() or modulate();
        same underlying thing.
        @return The value that low C has been moved up to.
    */
    public int getTranspose() {
        return getNoteAttributesForRead().transpose;
    }


    protected abstract NoteAttributes getNoteAttributesForRead();
    protected abstract T setVolume(int v);
    protected abstract T setTranspose(int semitones);

}
