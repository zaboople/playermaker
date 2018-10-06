package test.hear2;
import org.tmotte.pm2.MyMidi3;
import org.tmotte.pm2.Player;
import static org.tmotte.pm2.Pitches.*;

/**
 * Tests out the Rest.finish() shortcut, and even Rest.t() as well.
 */
public class TestFinish implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestFinish().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .setBeatsPerMinute(60)
		    .instrumentChannel(43, 3)
		    .setBendSensitivity(4)
		    .reverb(50)
		    .r(4)

		    .octave(4)
		    .c(1, D).t(4)

			    .r(8)
				    .finup(A)
			    .r(4)
				    .finup(B-12)
			    .r(4.)
				    .finup(E)
			    .r(2)
				    .finup(G)
			    .r(2).t(8)
				    .finup(A+12)
				    .up()

			.r(4);
	    midi.play(stop, player);
    }

}
