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
    boolean notDefault=false;


    public T volume(int v) {
        return setVolume(v);
    }
    public int volume() {
        return getAttributesForRead().volume;
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
            getAttributesForRead().transpose+semitones
        );
    }
    public int getTranspose() {
        return getAttributesForRead().transpose;
    }

    /**
     * Adds the given amount to the current volume setting.
     */
    public T addVolume(int change) {
        return volume(getAttributesForRead().volume+change);
    }

    public int getVolume() {
        return getAttributesForRead().volume;
    }

    protected abstract Attributes getAttributesForRead();
    protected abstract T setVolume(int v);
    protected abstract T setTranspose(int semitones);

}
