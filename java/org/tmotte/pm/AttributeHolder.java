package org.tmotte.pm;

/**
 * This is extended by Chord, Note & Player. Player passes its attributes on to the Chord (when created, for example, by Player#c()),
 * and Chord passes its on to Note. Chord and Note can in turn override the passed-on settings.
 * <br>
 * The only actual things set in here are octave/transpose & volume.
 * <br>
 * FIXME I bet settings aren't passed on to Note, since you change Chord after its notes.
 */
public abstract class AttributeHolder<T> {
    private int volume=64, transpose=0;
    protected AttributeHolder() {
    }
    protected AttributeHolder(AttributeHolder<?> other) {
        this.volume=other.getVolume();
        this.transpose=other.getTranspose();
    }
    public int volume() {
        return volume;
    }
    public int getTranspose() {
        return transpose;
    }
    public int getVolume() {
        return volume;
    }
    /**
     * Sets the volume at a specific level.
     */
    public T volume(int v) {
        volume=v;
        return self();
    }
    /**
     * Adds the given amount to the current volume setting.
     */
    public T addVolume(int change) {
        return volume(volume+change);
    }
    /**
     * Select 0 for the default octave, or any positive number to modulate up by that many octaves.
     */
    public T octave(int octave) {
        transpose=octave*12;
        return self();
    }
    /**
     * Modulates by individual semitones, not octaves.
     */
    public T modulate(int semitones) {
        transpose+=semitones;
        return self();
    }

    protected abstract T self();
}
