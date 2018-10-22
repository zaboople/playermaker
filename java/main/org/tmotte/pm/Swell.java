package org.tmotte.pm;
import java.util.ArrayList;
import java.util.List;

final class Swell {


    /////////////////////////
    // INSTANCE VARIABLES: //
    /////////////////////////

    final long delay;
    final long duration;
    final int toVolume;

    long delay() {
        return delay;
    }
    /** This is not the duration as input, but converted to tick groups. FIXME rename durationTicks */
    long duration() {
        return duration;
    }
    int toVolume() {
        return toVolume;
    }

    Swell(long delay, long duration, int toVolume) {
        this.delay=delay;
        this.duration=duration;
        this.toVolume=toVolume;
    }


}