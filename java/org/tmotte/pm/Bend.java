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
 * <p>
 * Background: By default, a bend can go a "whole step", which is to say, two notes up or down.
 * 0 is all the way down, and 16384-1 is all the way up. So, 8192 is no bend at all.
 * Bends apply to the whole channel, regardless of what track they appear on. Finally,
 * the bend stays applied until it is unapplied.
 * <p>
 * However, you can change the "Bend sensitivity" (refer to the Player API) to increase
 * the range from a whole step to many more steps. The 16383/8192/0 limits remain the same.
 *
 * @see Player#setBendSensitivity(int)
 */
public final class Bend {

    /////////////////////
    // STATIC METHODS: //
    /////////////////////


    static <T extends BendContainer> T add(T container, int delay, int duration, int denominator) {
        fix(container).add(new Bend(Divisions.convert(delay), Divisions.convert(duration), denominator));
        return container;
    }
    static <T extends BendContainer> T add(T container, long delay, long duration, int denominator) {
        fix(container).add(new Bend(delay, duration, denominator));
        return container;
    }
    static <T extends BendContainer> T add(T container, double delay, double duration, int denominator) {
        fix(container).add(new Bend(Divisions.convert(delay), Divisions.convert(duration), denominator));
        return container;
    }
    private static List<Bend> fix(BendContainer container) {
        List<Bend> bends=container.getBends();
        if (bends==null) {
            bends=new ArrayList<>();
            container.setBends(bends);
        }
        return bends;
    }


    static <T extends BendContainer> T vibrato(T container, int delay, int duration, int frequency, int denominator) {
        return vibrato(
            container,
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }
    static <T extends BendContainer> T vibrato(T container, double delay, double duration, double frequency, int denominator) {
        return vibrato(
            container,
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }
    //FIXME this and other variations....
    static <T extends BendContainer> T vibrato(T container, int delay, double duration, double frequency, int denominator) {
        return vibrato(
            container,
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }
    static <T extends BendContainer> T vibrato(
            T container, long delay, long duration, long frequency, int denominator
        ) {
        List<Bend> bends=fix(container);
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
        return container;
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

    public long delay() {
        return delay;
    }
    /** This is not the duration as input, but converted to tick groups. FIXME rename durationTicks */
    public long duration() {
        return duration;
    }
    public int denominator() {
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