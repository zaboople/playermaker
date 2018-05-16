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

    public Player p(double duration, int... notes) {
        return c(duration, notes).up();
    }
    public Sound c(double duration, int... notes) {
        return c(Divisions.convert(duration), notes);
    }
    public Note n(double duration, int note) {
        return n(Divisions.convert(duration), note);
    }

    public Player p(int duration, int... notes) {
        return c(duration, notes).up();
    }
    public Sound c(int duration, int... notes) {
        return c(Divisions.convert(duration), notes);
    }
    public Note n(int duration, int note) {
        return n(Divisions.convert(duration), note);
    }

    public Player p(long duration, int... notes) {
        return c(duration, notes).up();
    }
    public Sound c(long duration, int... notes) {
        for (int n: notes)
            add(duration, n);
        return sound;
    }
    public Note n(long duration, int note) {
        return add(duration, note);
    }

    private Note add(long duration, int pitch) {
        return sound.addNote(duration, restFor, pitch);
    }

}