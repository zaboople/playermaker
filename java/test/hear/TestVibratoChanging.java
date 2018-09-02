package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestVibratoChanging implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVibratoChanging().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .instrumentChannel(0,  0)
		    .bendSense(8)
		    .octave(2)
		    .r(4)
			.p32(B).p32(A).p32(B).p32(A)
			.p32(B).p32(A).p32(B).p32(A)
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
			.p32(B).p32(A).p32(B).p32(A)
			.p32(B).p32(A).p32(B).p32(A)
			.p32(A, C).p32(A, B).p32(B, C).p32(A, B)
			.p32(A, C).p32(A, B).p32(B, C).p32(A, B)
			.p32(B).p32(A).p32(B).p32(A)
			.p32(B).p32(A).p32(B).p32(A)
			.octave(3)
			.c(1, B, D_, F)
				.vibrato(4, 64, 16)
				.vibrato(4, 32, 16)
				.vibrato(2, 16, 16)
				.up()
			.r4();
	    midi
		    .setBeatsPerMinute(60)
		    .play(stop, player);
    }

}
