package org.tmotte.pm;

class Attributes {
    int volume=64, transpose=0, pressure=0;
    Attributes() {
    }
    Attributes(Attributes other) {
        this.volume=other.volume;
        this.transpose=other.transpose;
        this.pressure=other.pressure;
    }

}
