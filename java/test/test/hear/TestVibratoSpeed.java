package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

/** This is running vibratos using dotted notes */
public class TestVibratoSpeed implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVibratoSpeed().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
			.setBeatsPerMinute(100)
		    .instrumentChannel(23, 0)
		    .setBendSensitivity(4)
		    .octave(5)
		    .r(4)

			.c(1, F-12, C, G)
				.bend(4, 2)
				.vibrato(2., 32, 16).up()

			.c(4, F-12, C, G)
				.r(8).up(16, G-12)
				.r(8.).up(16, B-12)
				.up()
			.c(2., E-12, G-12, F)
				.vibrato(32, 16).up()

			.c(2, A-12, C, E)
				.bend(4, 2)
				.vibrato(32, 16).up()
			.p(8, E)
			.p(8, F)
			.c(2, D_, E, G_)
				.vibrato(32, 16).up()
			.p(8, F)
			.p(8, G_)
			.c(2, E, G_-12, B_)
				.vibrato(32, 16).up()

			.c(4, A)
				.vibrato(32, 16).up()
			.c(2., C+12, E, G)
				.vibrato(32, 16).up()

		    .r(4);
	    midi.play(stop, player);
    }
}
