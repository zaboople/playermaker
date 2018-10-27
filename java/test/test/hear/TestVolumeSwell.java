package test.hear;
import javax.sound.midi.Instrument;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.common.text.Log;
import static org.tmotte.pm.Pitches.*;

/**
 * Tests volume swell up & down.
 */
public class TestVolumeSwell  implements XTest {
    public static void main(String args[]) throws Exception {
        Log.add("SwellGen", "MyMidi3" /*, "MidiTracker"*/);
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

				.c(2, B, D+12).volume(0).swell(16, mainVolume).swell(0)
					.up()
				.r(8)
				.c(2, C, G).volume(0).swell(16, mainVolume).swell(0)
					.up()
				.r(8)
				.c(2, G, E+12).volume(0).swell(16, mainVolume).swell(0)
					.up()
				.r(8)
				.c(2, B, G).volume(0).swell(16, mainVolume).swell(0)
					.up()
				.r(8)

				.octave(4)
				.p(8., E, G)
				.p(16, E)
				.p(16, D)
				.p(8., E, C-12)

				.r(8)

				.p(8., D, B)
				.p(16, B-12)
				.p(16, D)
				.p(8., F, D-12)
				.r(8)

				.c(8., G-12, G).up()
				.p(16, A)
				.p(4, C)
				.c(2, G-12, D).swell(4., 8, 0).up()


				.r(4)
		);
    }
}
