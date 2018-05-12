package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;

public class TestBends2  {
    public static void main(String args[]) throws Exception {
	    //Organ 1, with double bend-sensitivity:
	    Player player=new Player()
		    .instrumentTrackChannel(16, 0, 0)
		    .setBendSensitivity(4);
		int base=10;
	    player
		    .octave(6)
		    .r4()
			.s4(G, B, D).bend(32, 1).up()
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
	    new MyMidi3()
		    .setBeatsPerMinute(60)
		    .sequenceAndPlay(player, true);
	    System.out.println("Done");
    }

}
