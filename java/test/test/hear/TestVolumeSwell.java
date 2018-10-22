package test.hear;
import javax.sound.midi.Instrument;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.common.text.Log;
import static org.tmotte.pm.Pitches.*;

/**
 * In other words, 7/16 time, seven beats per measure, 16th note gets a beat.
 */
public class TestVolumeSwell  implements XTest {
    public static void main(String args[]) throws Exception {
        Log.add("SwellGen", "MyMidi3", "MidiTracker");
	    new TestVolumeSwell().test(new MyMidi3(), true);
	}
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    int mainVolume=110;
		midi.play(
			stop,
			new Player()
				.setBPM(70)
				.setReverb(32)
				.setPressure(32)
				.instrument("Strings - Viola")
				.channel(0)
				.pressure(100)
				.volume(mainVolume)
				.octave(3)
				.r(4)

				//FIXME broken this is overlapping because the swell(0) is not a remainder
				.c(2, B, D+12).volume(0).swell(16, mainVolume).swell(0)
					.up()
				.r(4)
				.c(2, C, G).volume(0).swell(16, mainVolume).swell(0)
					.up()
				.r(8)
				.c(2, G, E+12).volume(0).swell(16, mainVolume).swell(0)
					.up()
				.r(8)
				.c(2, B, G).volume(0).swell(16, mainVolume).swell(0)
					.up()

				.octave(4)
				.r(8)
				.p(4, E, G)
				.p(16, E)
				.p(16, D)
				.p(4, E, C-12)
				.r(8)
				.p(4, D, B)
				.p(16, E)
				.p(16, F)
				.p(4, F, C-12)
				.r(8)
				.p(16, G)
				.p(8., C)
				.c(2, G-12, D).volume(mainVolume).swell(4., 8, 0).up()


				.r(4)
		);
    }
}
