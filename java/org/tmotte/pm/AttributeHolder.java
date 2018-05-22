package org.tmotte.pm;

public abstract class AttributeHolder<T> {
    TonalAttributes attrs;
    public AttributeHolder(TonalAttributes attrs) {
        this.attrs=attrs;
    }
    public int volume() {
        return attrs.volume;
    }
    public T volume(int v) {
        attrs.volume=v;
        return self();
    }
    public T addVolume(int change) {
        attrs.addVolume(change);
        return self();
    }
    public T octave(int octave) {
        attrs.octave(octave);
        return self();
    }
    public T move(int octaves) {
        attrs.move(octaves);
        return self();
    }
    public T modulate(int notes) {
        attrs.modulate(notes);
        return self();
    }
    TonalAttributes attrs() {
        return attrs;
    }

    protected abstract T self();
}
