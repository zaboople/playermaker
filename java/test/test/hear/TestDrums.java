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
	    Log.with(
		    ()-> new TestDrums().test(new MyMidi3(), true),
		    "MyMidi33"
	    );
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    // This has two drummers, which means they gotta share a
	    // channel. It's ok.
	    final int bpm = 25;
	    Player bass=new Player(0)
		    .instrument(midi.findInstrument("Baritone Sax"))
		    //.instrument(midi.findInstrument("Brass Tuba"))
		    .volume(100)
		    .reverb(64)
		    .setBeatsPerMinute(bpm)
		    .octave(2);
	    Player drummer2=Player.drummer()
		    .instrument(midi.findInstrument("Drum Jazz"))
		    .volume(110)
		    .reverb(32)
		    .setBeatsPerMinute(bpm)
		    .octave(2);
	    Player drummer1=Player.drummer()
		    .instrument(midi.findInstrument("Drum Jazz"))
		    .volume(110)
		    .reverb(32)
		    .setBeatsPerMinute(bpm)
		    .octave(2);
	    int bigTom = F+12+12+12;
	    int shortHat = F+1+12;
	    int crash = C+1+12+12;
	    int snare = D+12;
	    int bassdrum = B;
	    //bass.r(16);
	    drummer1.r(32);
	    for (int i=0; i<6; i++)
		    drummer1.p(64, bassdrum).p(64, bassdrum).r(16.);

	    for (int i=0; i<2; i++)
		    for (int j: Arrays.asList(0, 3, 2, 5))
			    bass
				    .p(64, A+j)
				    .r(32.);
	    bass.octave(3).volume(60).p(8, A, C-12, C, E, A-12);

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

		    .p(16, snare)
		    .p(64, shortHat)
		    .p(tie(16, -64), snare)
		    .p(64, shortHat)
		    .p(tie(16, -64), snare)
		    .p(64, shortHat)
		    .p(tie(16, -64), crash)

		    .p(8, bassdrum)
		    .r(8)
		    ;
	    midi.playAndStop(bass, drummer2, drummer1);
    }

}
