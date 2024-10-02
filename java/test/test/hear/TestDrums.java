package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.common.text.Log;

import static org.tmotte.pm.Tie.tie;
import static org.tmotte.pm.Pitches.*;

/** This should play 2 rolls per second. */
public class TestDrums implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestDrums().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Log.add("MyMidi3");
	    final int bpm = 30;
	    Player drummer1=Player.drummer()
		    .instrument(midi.findInstrument("Drum Orchestra"))
		    .volume(110)
		    .reverb(64)
		    .setBeatsPerMinute(bpm)
		    .octave(2);
	    Player drummer2=Player.drummer()
		    .instrument(midi.findInstrument("Drum Orchestra"))
		    .volume(110)
		    .reverb(64)
		    .setBeatsPerMinute(bpm)
		    .octave(2);
	    int bigTom = F + (3*12);
	    int shortHat = E;
	    drummer2
		    .c(4, bigTom)
			    .r(16).c(16, shortHat).up()
			    .r(16., 64).c(16, shortHat).up()
			    .r(16, 16).c(16, shortHat).up()
			    .up()
		    .p(16, E+12)
		    .p(64, E)
		    .p(16, E+12)
		    .p(64, E)
		    .p(16, E+12)
		    .r(4)
		    ;
	    midi.playAndStop(drummer1, drummer2);
    }

}
