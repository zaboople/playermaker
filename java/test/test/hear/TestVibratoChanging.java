package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestVibratoChanging implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVibratoChanging().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .setBeatsPerMinute(60)
		    .instrument("Piano - Piano 1")
		    .bendSense(8)
		    .octave(2)
		    .r(4)
			.p(32, B).p(32, A).p(32, B).p(32, A)
			.p(32, B).p(32, A).p(32, B).p(32, A)
		    .octave(5)
			.c(2, A, D)
				.vibrato(8, 4, 16)
				.vibrato(8, 8, 12)
				.vibrato(8, 16, 8)
				.vibrato(8, 32, 4)
				.up()
			.c(2, B)
				.vibrato(4, 12)
				.up()
			.c(8, B_)
				.vibrato(64, 32)
				.up()
			.c(4, E, C+12)
				.vibrato(64, 16)
				.up()
			.octave(2)
			.p(32, B).p(32, A).p(32, B).p(32, A)
			.p(32, B).p(32, A).p(32, B).p(32, A)
			.p(32, A, C).p(32, A, B).p(32, B, C).p(32, A, B)
			.p(32, A, C).p(32, A, B).p(32, B, C).p(32, A, B)
			.p(32, B).p(32, A).p(32, B).p(32, A)
			.p(32, B).p(32, A).p(32, B).p(32, A)
			.octave(3)
			.c(1, B, D_, F)
				.vibrato(4, 64, 16)
				.vibrato(4, 32, 16)
				.vibrato(2, 16, 16)
				.up()
			.r(4);
	    midi.play(stop, player);
    }

}
