package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestVibrato implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVibrato().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .instrumentTrackChannel(16, 0, 0)
		    .setBendSensitivity(4)
		    .octave(5)
		    .r4()
			.s4(A-12).vibrato(64, 12).up()
			.p16(B-12)
			.p16(C)
			.s4(D).vibrato(64, 12).up()
			.s2(C, E, G)
				.bend(4, 2)
				.vibrato(4, 64, 6)
				.up()
			.p8(C, G_, A)
			.s2(B, D_, F).vibrato(64, 12).up()
		    .r4();
	    midi
		    .setBeatsPerMinute(60)
		    .play(stop, player);
    }

}
