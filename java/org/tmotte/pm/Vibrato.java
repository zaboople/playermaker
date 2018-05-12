package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;


/**
 * Vibrato via bends might seem like an overly intense thing to do, but
 * it's really the same overhead as a bend. Both are evenly/slowly spaced out
 * over many events during sequencing.
 * FIXME DELETE CLASS
 */
public final class Vibrato {
    final long delay;
    final long duration;
    final long frequency;
    final int denominator;

    /**
     *
     */
    public Vibrato(long delay, long duration, long frequency, int denominator) {
        this.delay=Divisions.convert(delay);
        this.duration=Divisions.convert(duration);
        this.frequency=Divisions.convert(frequency);
        this.denominator=denominator;
        System.out.println("DUR "+this.duration);
    }

    /**
     *
     */
    public Vibrato(double delay, double duration, double frequency, int denominator) {
        this.delay=Divisions.convert(delay);
        this.duration=Divisions.convert(duration);
        this.frequency=Divisions.convert(frequency);
        this.denominator=denominator;
    }

    public long delay() {
        return delay;
    }
    public long frequency() {
        return frequency;
    }
    public int denominator() {
        return denominator;
    }

    public static List<Vibrato> add(List<Vibrato> vibratos, long delay, long duration, long frequency, int denominator) {
        if (vibratos==null)
            vibratos=new ArrayList<>();
        vibratos.add(new Vibrato(delay, duration, frequency, denominator));
        return vibratos;
    }
    public static List<Vibrato> add(List<Vibrato> vibratos, double delay, double duration, double frequency, int denominator) {
        if (vibratos==null)
            vibratos=new ArrayList<>();
        vibratos.add(new Vibrato(delay, duration, frequency, denominator));
        return vibratos;
    }

}