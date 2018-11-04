package org.tmotte.pm;
import org.tmotte.common.midi.MidiTracker;
import java.util.function.LongSupplier;
import java.util.List;
import org.tmotte.common.text.Log;

/** Generates volume swells; only used by MyMidi3. */
class SwellGen  {
	static @FunctionalInterface interface ExpressionSender{
		void sendExpression(int channel, int volume, long tick);
	}

	private final LongSupplier tickXget;
	private final ExpressionSender midiTracker;
	SwellGen(LongSupplier tickXget, ExpressionSender midiTracker) {
		this.tickXget=tickXget;
		this.midiTracker=midiTracker;
	}

    void handle(
            final int channel,
            long startTick,
            int startVolume,
            final List<Swell> swells
        ) {
        Log.log("SwellGen", "doSwell(): startTick {} startVolume {} ", startTick, startVolume);

		// Tickx doesn't change during composition, but we're gonna use injection to get it:
        final long tickX=tickXget.getAsLong();

		// Dynamic variable for volume:
        int volume=startVolume;

		// Loop thru swells, changing timing & volume as we go:
        for (Swell swell: swells) {

	        // A bunch of constants
	        final long delay=swell.delay() * tickX,
			            duration=swell.duration() * tickX;
            final int toVolume=swell.toVolume();
            final int change=toVolume-volume;
            final int absChange=Math.abs(change)+1,
			            increment=change > 0 ?1 :-1;
            final long ticksPer = duration / absChange;

            // If there is a delay, MyMidi sent note on at 127, so we need to
            // bring that down to the intended starting level using expression:
            if (delay>0)
                midiTracker.sendExpression(channel, volume, startTick);
	        startTick+=delay;

			// Dynamic timing variables:
            long leftoverTicks = duration % absChange;
            long t=startTick;

			// Do some logging:
            Log.log("SwellGen", "doSwell loop: startTick {} duration {} ticksPer {}", t, duration, ticksPer);
            Log.log("SwellGen", "              volume {} toVolume {} change {} increment {}", startVolume, toVolume, change, increment);

            // Now loop thru the ticks, incrementing volume by one each time.
            // Then set up our new starting volume and time
            boolean[] spread=Spreader.array(absChange, (int)leftoverTicks);
            for (int i=0; i<absChange; i++) {
                midiTracker.sendExpression(channel, volume, t);
                volume+=increment;
                t+=ticksPer + (spread[i] ?1 :0);
            }
            startTick+=duration;
            volume=toVolume;
        }
    }


}
