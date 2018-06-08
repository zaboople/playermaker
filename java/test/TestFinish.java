package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

/**
 * Tests out the Rest.finish() shortcut, and even Rest.t() as well.
 */
public class TestFinish  {
    public static void main(String args[]) throws Exception {
	    Player player=new Player()
		    .instrumentChannel(43, 3)
		    .setBendSensitivity(4)
		    .r4()

		    .octave(4)
		    .c(1, D)
			    .r(8)
			    .fin(A)
			    .r(4)
			    .fin(B-12)
			    .r(4.)
			    .fin(E)
			    .r(2)
			    .fin(G)
			    .r(2).t(8)
			    .fin(A+12)
			    .t(4)
			    .up()

			.r4();
	    new MyMidi3()
		    .setBeatsPerMinute(60)
		    .playAndStop(player);
	    System.out.println("Done");
    }

}
