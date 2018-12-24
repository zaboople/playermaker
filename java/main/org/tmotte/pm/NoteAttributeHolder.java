package org.tmotte.pm;

/**
 * This is extended by Chord and Player. Player passes its attributes on to Chord when creating a Chord via {@link Player#c(int, int...)},
 * but Chord can override those settings. Chord passes its own attributes on to its sub-chords.
 * <br>
 * The only actual things set in here are octave/transpose &amp; volume.
 */
public abstract class NoteAttributeHolder<T> {

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
     */
    public T addVolume(int change) {
        return volume(getNoteAttributesForRead().volume+change);
    }
    public int volume() {
        return getNoteAttributesForRead().volume;
    }
    public int getVolume() {
        return getNoteAttributesForRead().volume;
    }


    /**
     * Select 0 for the default octave, or any positive number to modulate up from 0 by that many octaves.
     */
    public T octave(int octave) {
        return setTranspose(octave*12);
    }
    /**
     * Modulates by individual semitones, not octaves; adds to the current transposition
     * setting instead of treating semitones as an absolute value.
     */
    public T modulate(int semitones) {
        return setTranspose(
            getNoteAttributesForRead().transpose+semitones
        );
    }
    public int getTranspose() {
        return getNoteAttributesForRead().transpose;
    }



    protected abstract NoteAttributes getNoteAttributesForRead();
    protected abstract T setVolume(int v);
    protected abstract T setTranspose(int semitones);

}
