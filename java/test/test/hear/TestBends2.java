package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestBends2 implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBends2().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .setBeatsPerMinute(60)
		    .setBendSensitivity(4)
		    .instrument("Brass - French Horns")
		    .reverb(55)
		    .octave(5)
		    .r(4)
			.c(4, G+2, B+2, D+2)
				.bend(8, 2)
				.up()
			.p(4, A, D_, E)
			.p(8, D_, F)
			.c(4., C, E, G).vibrato(64, 18).up()
			.bendSense(4)
			.c(2, B_)
				.bend(8, 2)
				.vibrato(8, 64, 24)
				.bend(8, -2)
				.vibrato(8, 64, 24)
				.r(16).fin(D_).bwp()
					.r(16).fin(F).bwp()
						.up()
					.up()
				.up()
			.bendSense(4)
			.c(2, B_).t(4)
				.bend(16, -2)
				.bend(32, 32., 2)
				.bend(16, 32., -2)
				.bend(32, 32., 1)
				.bend(16, 32., -2)
				.up()
			.c(2, E, G_, B).vibrato(64, 18).up()
		    .r(4);
	    midi.play(stop, player);
    }

}
