package org.tmotte.pm;
import java.util.List;
import java.util.ArrayList;

public class TonalAttributes {
    int volume=64;
    int transpose=0;
    int speed=0;// wut fixme?

    TonalAttributes() {
    }

    public TonalAttributes(TonalAttributes other) {
        this.volume=other.volume;
        this.transpose=other.transpose;
        this.speed=other.speed;
    }

    public TonalAttributes addVolume(int change) {
        volume+=change;
        return this;
    }

    public TonalAttributes volume(int v) {
        volume=v;
        return this;
    }

    public TonalAttributes octave(int octave) {
        transpose=octave*12;
        return this;
    }
    public TonalAttributes move(int octaves) {
        transpose+=octaves*12;
        return this;
    }
    public TonalAttributes modulate(int notes) {
        transpose+=notes;
        return this;
    }

}
