package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;
import org.tmotte.common.text.Log;

/** Contains information about a Bend, created by Chord.bend() */
final class Bend {

    /////////////////////
    // STATIC METHODS: //
    /////////////////////


    static void add(List<Bend> bends, long delay, long duration, int denominator) {
        bends.add(new Bend(delay, duration, denominator));
    }

    static void vibrato(List<Bend> bends, long delay, long duration, long frequency, int denominator) {
        if (denominator==0)
            throw new RuntimeException("Denominator was 0");
        if (denominator % 2 != 0)
            throw new RuntimeException("Denominator should be divisible by 2; value was "+denominator);
        long count=duration/frequency;
        Log.log(
            "Bend", "Frequency: {} Duration: {} Count: {} Denom: {}",
            frequency, duration, count, denominator
        );
        int flipper=1;
        for (long i=0; i<count; i++) {
            add(bends, delay, frequency, denominator * (flipper*=-1));
            if (i==0) {
                delay=0;
                denominator/=2;
            }
        }
    }


    /////////////////////////
    // INSTANCE VARIABLES: //
    /////////////////////////

    final long delay;
    final long duration;
    final int denominator;

    //////////////////////////////
    // PUBLIC INSTANCE METHODS: //
    //////////////////////////////

    long delay() {
        return delay;
    }
    /** This is not the duration as input, but converted to our custom "tick" system. */
    long duration() {
        return duration;
    }
    int denominator() {
        return denominator;
    }

    //////////////////////////////////////////
    // PRIVATE METHODS (INCL. CONSTRUCTORS: //
    //////////////////////////////////////////


    protected Bend(long delay, long duration, int denominator) {
        this.delay=delay;
        this.duration=duration;
        this.denominator=denominator;
    }

}