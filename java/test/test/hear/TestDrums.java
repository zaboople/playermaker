package test.hear;
import java.util.Arrays;
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
	    final int bpm = 20;
	    Player bass=new Player(0)
		    .instrument(midi.findInstrument("Contrabass"))
		    .volume(80)
		    .reverb(64)
		    .setBeatsPerMinute(bpm)
		    .octave(2);
	    Player drummer2=Player.drummer()
		    .instrument(midi.findInstrument("Drum Orchestra"))
		    .volume(110)
		    .reverb(32)
		    .setBeatsPerMinute(bpm)
		    .octave(2);
	    int bigTom = F + (3*12);
	    int shortHat = E;
	    for (int i=0; i<2; i++)
		    for (int j: Arrays.asList(0, 3, 2, 5))
			    bass
				    .p(32, A+j)
				    .r(32);
	    bass.octave(3).volume(60).p(8, A, C, E);
	    drummer2
		    .p(16, bigTom)
		    .p(tie(16, -64), shortHat)
		    .p(64, shortHat)
		    .p(tie(16, -64), shortHat)
		    .p(64, shortHat)
		    .p(16, shortHat)

		    .p(16, bigTom)
		    .p(tie(16, -64), shortHat)
		    .p(64, shortHat)
		    .p(tie(16, -64), shortHat)
		    .p(64, shortHat)
		    .p(16, shortHat)

		    .p(16, E+12, F)
		    .p(64, E)
		    .p(tie(16, -64), E+12)
		    .p(64, E)
		    .p(tie(16, -64), E+12)
		    .p(64, E)
		    .p(tie(16, -64), F)

		    .r(8)
		    ;
	    midi.playAndStop(bass, drummer2);
    }

}
