package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;

    /**
     * Same as Bend(int, int, int) but the doubles are for expressing
     * <ul>
     *   <li>Triplets - e.g. 8.3 means a single eighth-note from a triplet
     *   <li>Dotted notes - e.g. 8.2 means a dotted eighth-note. FIXME
     *      how to delay/duration 8.3 but twice???? 16.6??? 8.6???
     * <li>
     * FIXME
     */

/**
 * FIXME WHY OH WHY DID YOU DO "DENOMINATOR"? Why couldn't you just send... well
 *
 * @see Player#setBendSensitivity(int)
 */
final class Bend {

    /////////////////////
    // STATIC METHODS: //
    /////////////////////


    static void add2(BendContainer<?> container, long delay, long duration, int denominator) {
        fix2(container).add(new Bend(delay, duration, denominator));
    }
    static void add2(BendContainer<?> container, int delay, int duration, int denominator) {
        fix2(container).add(new Bend(Divisions.convert(delay), Divisions.convert(duration), denominator));
    }
    static void add2(BendContainer<?> container, double delay, double duration, int denominator) {
        fix2(container).add(new Bend(Divisions.convert(delay), Divisions.convert(duration), denominator));
    }

    private static List<Bend> fix2(BendContainer<?> container) {
        List<Bend> bends=container.getBends();
        if (bends==null) {
            bends=new ArrayList<>();
            container.setBends(bends);
        }
        return bends;
    }

    static void vibrato2(BendContainer<?> container, long delay, long duration, long frequency, int denominator) {
        List<Bend> bends=fix2(container);
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