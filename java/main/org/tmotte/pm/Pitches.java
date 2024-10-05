package org.tmotte.pm;

/** Contains convenient static variables for C thru B notes. */
public class Pitches {
    private Pitches(){}

    /** Contains regular notes and flatted notes; the latter represented by "_" in name.
        Never came up with a way to say "sharp" */
    public static final int C=0, D_=1, D=2, E_=3, E=4, F=5, G_=6, G=7, A_=8, A=9, B_=10, B=11;
}