package org.tmotte.pm2;

/**
 * This is only used by NoteAttributeHolder, and only contains
 * volume &amp; transpose.
 */
class NoteAttributes {
    int volume=64, transpose=0;
    NoteAttributes() {
    }
    NoteAttributes(NoteAttributes other) {
        this.volume=other.volume;
        this.transpose=other.transpose;
    }

}
