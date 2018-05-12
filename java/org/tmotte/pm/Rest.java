package org.tmotte.pm;

/**
 * Rest acts as a temporary placeholder, only used by Sound (not Player); the intention is that you
 * want to create a chord where notes are delayed for an arpeggiated effect.
 */
public class Rest {
    final Sound sound;
    final long restFor;

    public Rest(Sound sound, long restFor) {
        this.sound=sound;
        this.restFor=restFor;
    }

    public Player p4(int note) {
        return s4(note).up();
    }
    public Player p8(int note) {
        return s8(note).up();
    }
    public Player p16(int note) {
        return s16(note).up();
    }

    public Sound s4(int note) {
        return n4(note).up();
    }
    public Sound s8(int note) {
        return n8(note).up();
    }
    public Sound s16(int note) {
        return n16(note).up();
    }

    public Note n4(int note) {
        return add(Divisions.reg4, note);
    }
    public Note n8(int note) {
        return add(Divisions.reg8, note);
    }
    public Note n16(int note) {
        return add(Divisions.reg16, note);
    }

    private Note add(long duration, int pitch) {
        return sound.addNote(duration, restFor, pitch);
    }

}