package org.tmotte.pm;
import org.tmotte.common.midi.MidiTracker;
import java.util.function.LongSupplier;
import java.util.List;
import org.tmotte.common.text.Log;

/** Generates bends; only used by MyMidi3. */
class BendGen  {

	private final static int NO_BEND = 8192;
	static @FunctionalInterface interface BendSender {
		void eventBend(int channel, int volume, long tick);
	}

	private final LongSupplier tickXget;
	private final BendSender midiTracker;


	BendGen(LongSupplier tickXget, BendSender midiTracker) {
		this.tickXget=tickXget;
		this.midiTracker=midiTracker;
	}

    void handle(int channel, long soundStart, List<Bend> bends) {
        Log.log("BendGen", "sendBends size: "+bends.size());
        long tickX=tickXget.getAsLong();
        long t=soundStart;
        int pitch=NO_BEND;
        for (Bend bend: bends) {
            Log.log("BendGen", "Bend delay {} duration {} denominator {} ", bend.delay, bend.duration, bend.denominator);
            t+=(bend.delay * tickX);
            int change = NO_BEND / bend.denominator;
            int realDuration=-1 + (int)bend.duration;
            int perTicky = change / realDuration;
            int leftover = change % realDuration;
            int leftoverIncr=leftover>0 ?1 :-1;
            Log.log("BendGen", " change: {} perTicky {} leftover {}", change, perTicky, leftover);
            for (int i=0; i<bend.duration; i++) {
                if (pitch==16384) pitch=16383;
                if (pitch > 16383 || pitch < 0)
                    throw new RuntimeException("You bent too far, probably by doing multiple bends");
                midiTracker.eventBend(channel, pitch, t);
                t+=tickX;
                int thisAmount = perTicky;
                if (leftover!=0) {
                    thisAmount+=leftoverIncr;
                    leftover-=leftoverIncr;
                }
                pitch+=thisAmount;
            }
        }
    }

}
