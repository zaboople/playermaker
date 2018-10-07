package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestVibrato implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVibrato().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .setBeatsPerMinute(60)
		    .instrument(16)
		    .setBendSensitivity(4)
		    .octave(5)
		    .r(4)
			.c(4, A-12)
				.vibrato(64, 12)
				.up()
			.p(16, B-12)
			.p(16, C)
			.c(4, D)
				.vibrato(64, 12)
				.up()
			.c(2, C, E, G)
				.bend(4, 2)
				.vibrato(4, 64, 6)
				.up()
			.p(8, C, G_, A)
			.c(2, B, D_, F)
				.vibrato(64, 12)
				.up()
		    .r(4);
	    midi.play(stop, player);
    }

}
