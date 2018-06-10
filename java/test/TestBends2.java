package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestBends2 implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBends2().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 sqr, boolean stop)  {
	    //Organ 1, with double bend-sensitivity:
	    Player player=new Player()
		    .instrumentTrackChannel(16, 0, 0)
		    .setBendSensitivity(4);
		int base=10;
	    player
		    .octave(5)
		    .r4()
			.s4(G+2, B+2, D+2).bend(8, 2).up()
			.p4(A, D_, E)
			.p8(E, D_, F)
			.s4(C, E, G)
				// This is basically vibrato:
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)

				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.bend(64, 8).bend(64, -8)
				.up()
		    .r4();
	    sqr
		    .setBeatsPerMinute(60)
		    .playAndStop(player);
	    System.out.println("Done");
    }

}
