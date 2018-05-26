package org.tmotte.pm;

/**
 * In the case of Player,
 * <ul>
        <li>pX() creates a Sound, and returns the Player. Calling p/s/nX() again
           will create a new Sound that <em>follows</em> the first. Thus the Sound
           is committed: It cannot be further modified, and additional Sound/Note
           objects will play after it.
        <li>sX() creates &amp; returns a Sound object representing a chord, allowing
            for further modification of itself.
        <li>nX() creates a Sound with one Note, and returns that Note directly. This
            allows detailed modification (vibrato, bend) of only that Note. You can
            still go Note.up() back to the Sound and add more notes, etc.
   </ul>
   But in the case of Sound,
   <ul>
        <li>pX() adds notes to the existing chord, then returns Player. The Sound is
            committed and later Sounds/Notes will be played after it.
        <li>sX() does the same as Player.sX(), but appends to the chord to the existing
            notes in the Sound, rather than making them come _after_.
        <li>nX() does the same as Player.sX()
    <ul/>
    FIXME make an interface with default methods.
 */
public interface Notable {

    Note addNote(long duration, int note);
    Sound addSound(long duration, int... notes);

    public default Player p(Duration d, int... notes) {
        return c(d, notes).up();
    }
    public default Sound c(Duration d, int... notes) {
        return addSound(d.duration(), notes);
    }
    public default Note n(Duration d, int note) {
        return addNote(d.duration(), note);
    }

    public default Player p(int duration, int... notes) {
        return c(duration, notes).up();
    }
    public default Sound c(int duration, int... notes) {
        return addSound(Divisions.convert(duration), notes);
    }
    public default Note n(int duration, int note) {
        return addNote(Divisions.convert(duration), note);
    }

    public default Player p(double duration, int... notes) {
        return c(duration, notes).up();
    }
    public default Sound c(double duration, int... notes) {
        return addSound(Divisions.convert(duration), notes);
    }
    public default Note n(double duration, int note) {
        return addNote(Divisions.convert(duration), note);
    }


    ////////////////////////////////////////////
    // Old way of adding notes... not all bad //
    // but kind of repetitive to maintain:    //
    ////////////////////////////////////////////

    public default Note n1(int note) {
        return addNote(Divisions.whole, note);
    }
    public default Note n2(int note) {
        return addNote(Divisions.reg2, note);
    }
    public default Note n4(int note) {
        return addNote(Divisions.reg4, note);
    }
    public default Note n8(int note) {
        return addNote(Divisions.reg8, note);
    }
    public default Note n16(int note) {
        return addNote(Divisions.reg16, note);
    }
    public default Note n32(int note) {
        return addNote(Divisions.reg32, note);
    }
    public default Note n64(int note) {
        return addNote(Divisions.reg64, note);
    }


    public default Sound s1(int... notes) {
        return addSound(Divisions.whole, notes);
    }
    public default Sound s2(int... notes) {
        return addSound(Divisions.reg2, notes);
    }
    public default Sound s4(int... notes) {
        return addSound(Divisions.reg4, notes);
    }
    public default Sound s8(int... notes) {
        return addSound(Divisions.reg8, notes);
    }
    public default Sound s16(int... notes) {
        return addSound(Divisions.reg16, notes);
    }
    public default Sound s32(int... notes) {
        return addSound(Divisions.reg32, notes);
    }
    public default Sound s64(int... notes) {
        return addSound(Divisions.reg64, notes);
    }

    public default Sound s8_3(int... notes) {
        return addSound(Divisions.triplet8, notes);
    }
    public default Sound s16_3(int... notes) {
        return addSound(Divisions.triplet16, notes);
    }
    public default Sound s32_3(int... notes) {
        return addSound(Divisions.triplet32, notes);
    }
    public default Sound s64_3(int... notes) {
        return addSound(Divisions.triplet64, notes);
    }

    public default Player p1(int... notes) {
        return s1(notes).up();
    }
    public default Player p2(int... notes) {
        return s2(notes).up();
    }
    public default Player p4(int... notes) {
        return s4(notes).up();
    }
    public default Player p8(int... notes) {
        return s8(notes).up();
    }
    public default Player p16(int... notes) {
        return s16(notes).up();
    }
    public default Player p32(int... notes) {
        return s32(notes).up();
    }
    public default Player p64(int... notes) {
        return s64(notes).up();
    }

    public default Player p8_3(int... notes) {
        return s8_3(notes).up();
    }
    public default Player p16_3(int... notes) {
        return s16_3(notes).up();
    }
    public default Player p32_3(int... notes) {
        return s32_3(notes).up();
    }
    public default Player p64_3(int... notes) {
        return s64_3(notes).up();
    }

    public default Player p83(int... notes) {
        return s8_3(notes).up();
    }
    public default Player p163(int... notes) {
        return s16_3(notes).up();
    }
    public default Player p323(int... notes) {
        return s32_3(notes).up();
    }
    public default Player p643(int... notes) {
        return s64_3(notes).up();
    }

}
