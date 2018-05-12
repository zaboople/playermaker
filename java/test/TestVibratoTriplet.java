package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;

public class TestVibratoTriplet  {
    public static void main(String args[]) throws Exception {
	    //Organ 1, with double bend-sensitivity:
	    Player player=new Player()
		    .instrumentTrackChannel(16, 0, 0)
		    .setBendSensitivity(4)
		    .octave(5)
		    .r4()

			.s4(A-12, D_-12).vibrato(64, 10).up()
			.p8_3(B_, D)
			.s8_3(C, E_).vibrato(64, 10).up()
			.p8_3(E, A_)
			.s4(D_, A, G).vibrato(64, 10).up()

		    .r8()
			;
	    new MyMidi3()
		    .setBeatsPerMinute(40)
		    .sequenceAndPlay(player, true);
	    System.out.println("Done");
    }

}
