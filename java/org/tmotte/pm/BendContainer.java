package org.tmotte.pm;
import java.util.List;

/** This is an interface because I want default methods i.e. multiple inheritance. */
public interface BendContainer<T> {
    public List<Bend> getBends();
    public void setBends(List<Bend> bends);
    public T self();
    public long totalDuration();

    public default T bend(int denominator) {
        return bend(0L, totalDuration(), denominator);
    }
    public default T bend(long duration, int denominator) {
        return bend(0L, duration, denominator);
    }
    public default T bend(long delay, long duration, int denominator) {
        Bend.add2(this, delay, duration, denominator);
        return self();
    }

    public default T bend(double duration, int denominator) {
        return bend(0D, duration, denominator);
    }
    public default T bend(double delay, double duration, int denominator) {
        Bend.add2(this, delay, duration, denominator);
        return self();
    }

    public default T bend(int duration, int denominator) {
        return bend(0, duration, denominator);
    }
    public default T bend(int delay, int duration, int denominator) {
       Bend.add2(this, delay, duration, denominator);
       return self();
    }



    public default T vibrato(long frequency, int denominator) {
        return vibrato(0L, totalDuration(), frequency, denominator);
    }
    public default T vibrato(long duration, long frequency, int denominator) {
        return vibrato(0L, duration, frequency, denominator);
    }
    public default T vibrato(long delay, long duration, long frequency, int denominator) {
        Bend.vibrato2(this, delay, duration, frequency, denominator);
        return self();
    }

    public default T vibrato(int frequency, int denominator) {
        return vibrato(0L, totalDuration(), Divisions.convert(frequency), denominator);
    }
    public default T vibrato(int duration, int frequency, int denominator) {
        return vibrato(0, duration, frequency, denominator);
    }
    public default T vibrato(int delay, int duration, int frequency, int denominator) {
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }

    public default T vibrato(double frequency, int denominator) {
        return vibrato(0L, totalDuration(), Divisions.convert(frequency), denominator);
    }
    public default T vibrato(double duration, double frequency, int denominator) {
        return vibrato(0D, duration, frequency, denominator);
    }
    public default T vibrato(double delay, double duration, double frequency, int denominator) {
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }

    //FIXME this and other variations.... Maybe just do Number
    public default T vibrato(int delay, double duration, double frequency, int denominator) {
        return vibrato(
            Divisions.convert(delay),
            Divisions.convert(duration),
            Divisions.convert(frequency),
            denominator
        );
    }


}