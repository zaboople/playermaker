package org.tmotte.pm;

/** Contains convenient static variables for C thru B notes, including flats represented
    by "_" in the name (because "_" is... flat :| ) and sharps with an "s" instead.
    <p>
    And oh, yes, the lowest note in Midi is a C, just like a piano, and C is in fact
    the number 0, so B will will sound <i>above</i> C unless you use B+12 instead. Your
    actual lowest C on the java midi controller is nearly subsonic for most instruments,
    so you'll usually use {@link Player#octave} to move it up 2, maybe 3 or so octaves,
    except when playing drums.
    <p>
    Anyhow, this is all trivial convenience, and you're welcome to work out your own variables
    or just go the tough-guy way and play by integer.
*/
public class Pitches {
    private Pitches(){}

    /** Contains regular notes and flatted notes*/
    public static final int C=0, Cs=1, D_=1, D=2, Ds=3, E_=3, E=4, F=5, Fs=6, G_=6, G=7, Gs=8, A_=8, A=9, As=10, B_=10, B=11;
}