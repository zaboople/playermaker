package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;

/**
 * FIXME WHY OH WHY DID YOU DO "DENOMINATOR"? Why couldn't you just send... well
 *
 * @see Player#setBendSensitivity(int)
 */
final class Bend {

    /////////////////////
    // STATIC METHODS: //
    /////////////////////


    static void add(List<Bend> bends, long delay, long duration, int denominator) {
        bends.add(new Bend(delay, duration, denominator));
    }
    static void add(List<Bend> bends, int delay, int duration, int denominator) {
        bends.add(new Bend(Divisions.convert(delay), Divisions.convert(duration), denominator));
    }
    static void add(List<Bend> bends, double delay, double duration, int denominator) {
        bends.add(new Bend(Divisions.convert(delay), Divisions.convert(duration), denominator));
    }

    static void vibrato(List<Bend> bends, long delay, long duration, long frequency, int denominator) {
        if (denominator==0)
            throw new RuntimeException("Denominator was 0");
        if (denominator % 2 != 0)
            throw new RuntimeException("Denominator should be divisible by 2; value was "+denominator);
        long count=duration/frequency;
        System.out.println("Long, Frequency: "+frequency+" Duration: "+duration+" Count: "+count+" Denom:"+denominator);
        int flipper=1;
        for (long i=0; i<count; i++) {
            bends.add(new Bend(delay, frequency, denominator * (flipper*=-1))); //FIXME don't need bends=add(, just add(
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
    /** This is not the duration as input, but converted to tick groups. FIXME rename durationTicks */
    long duration() {
        return duration;
    }
    int denominator() {
        return denominator;
    }

    //////////////////////////////////////////
    // PRIVATE METHODS (INCL. CONSTRUCTORS: //
    //////////////////////////////////////////


    private Bend(long delay, long duration, int denominator) {
        this.delay=delay;
        this.duration=duration;
        this.denominator=denominator;
    }

}