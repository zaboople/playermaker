package test.hear2;
import javax.sound.midi.Instrument;
import org.tmotte.pm2.MyMidi3;
import org.tmotte.pm2.Player;
import static org.tmotte.pm2.Pitches.*;

/**
 * In other words, 7/16 time, seven beats per measure, 16th note gets a beat.
 */
public class Test7Slash16Time  implements XTest {
    public static void main(String args[]) throws Exception {
	    new Test7Slash16Time().test(new MyMidi3(), true);
	}
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    test(midi, stop, midi.getInstrument("Strings - Cello"));
    }
    public void test(MyMidi3 midi, boolean stop, Instrument instr) {
		midi.play(
			stop,
			new Player()
				.setBPM(70)
				.setReverb(64)
				.setPressure(32)
				.instrumentChannel(instr, 0)
				.r(4)
				.octave(3)


				// 1
				.p(4, F, B_, F+12)
				.r(8.)

				// 2
				.p(4, F, B_, F+12, A+12, C+24)
				.r(8.)

				// 3
				.p(4, A+12, C+24, E+24)
				.p(16, G+24)
				.p(16, F+24)
				.p(16, E+24)

				// 4
				.p(4, F, B_, F+12, A+12, C+12)
				.p(16, D+24)
				.p(16, E_+24)
				.c(16, E+12)

				// 1
					.t(4)
					.r(16)
						.up(4, F, B_+12, C+24, E+24)
					.up()
				.p(8., G+24)

				// 2
				.p(4, F, B_, F+12, A+12, C+24)
				.p(16, F+24)
				.c(8, F, B_+24)

				// 3
					.t(4)
					.r(8)
						.up(4, A+12, C+24, E+24)
					.up()
				.c(8., F, A+24)

				// 4
					.t(4)
					.r(8.)
						.up(4, F, B_, F+12, A+12, C+12)
					.up()
				.p(8, E+24)
				.p(16, F+24)

				// 1
				.p(16, F)
				.p(16, E)
				.p(8, A+12)
				.p(16, C+12)
				.p(16, E, F+24, C+24)
				.p(8, F, E+24, A+24)
				.c(16, F, B_, F+12, A+12, C+24)

				// 2
					.t(4)
					.up()
				.c(8., F, B_)

				// 3
					.t(4)
					.up()
				.p(8., C, E, A+12)

				// 4
				.p(8, F, A, C+12, F+12)
				.c(4, F, A, C+12, F+12)
					.t(16)

				// 1
					.t(8.)
					.up()

				.r(8)
		);
    }
}
