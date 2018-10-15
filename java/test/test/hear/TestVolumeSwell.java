package test.hear;
import javax.sound.midi.Instrument;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

/**
 * In other words, 7/16 time, seven beats per measure, 16th note gets a beat.
 */
public class TestVolumeSwell  implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVolumeSwell().test(new MyMidi3(), true);
	}
    public @Override void test(MyMidi3 midi, boolean stop)  {
		midi.play(
			stop,
			new Player()
				.setBPM(70)
				.setReverb(64)
				.setPressure(32)
				.instrument("Strings - Cello")
				.channel(0)
				.pressure(100)
				.octave(3)
				.r(4)
				.c(2, C).vibrato(64, 2).up()
				.r(4)
		);
    }
}
