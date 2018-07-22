package org.tmotte.pm;

/**
 * This is extended by Chord, Note & Player. Player passes its attributes on to Chord, and Chord
 * passes its on to Note. Chord and Note can in turn override the passed-on settings.
 * <br>
 * This is not an "inheritance" relationship, however. The passing-on is done in the AttributeHolder(other)
 * constructor.
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
    public T volume(int v) {
        volume=v;
        return self();
    }
    public T addVolume(int change) {
        volume+=change;
        return self();
    }
    public T octave(int octave) {
        transpose=octave*12;
        return self();
    }
    public T modulate(int semitones) {
        transpose+=semitones;
        return self();
    }

    protected abstract T self();
}
