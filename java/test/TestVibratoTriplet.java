package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;
import static org.tmotte.pm.Duration.*;

public class TestVibratoTriplet  {
    public static void main(String args[]) throws Exception {
	    //Organ 1, with double bend-sensitivity:
	    Player player=new Player()
		    .instrument(41)
		    .setBendSensitivity(4)
		    .setReverb(127)
		    .octave(5)
		    .r4()

			//1
			.c(4, A-12, D_-12).vibrato(64, 8).up()

			//2
			.p(8.3, B_, D)
			.c(8.3, C, E_).vibrato(64, 8).up()
			.p(8.3, E, A_)

			//3
			.s4(D_, A, G).vibrato(64, 8).up()

			//4
			.c(16, E_, C-24).vibrato(64, 8).up()
			.p(8., D)

			//5
			.p(16.3, C, D-24)
			.p(16.3, E_)
			.p(16.3, C, E_-24)
			.p8(D_)

			//6.1-2
			.p(16, C)
			.c(8., D_).vibrato(128, 10).up()

			//6.3-4
			.p(16.3, C, E-24)
			.p(16.3, E_)
			.p(16.3, C, D_-24)
			.r(8)

			//7-8
			.c(2, F-12, C, G)
				.bend(4, 2)
				.vibrato(4, 64, 8).up()

		    .r8()
			;
	    new MyMidi3()
		    .setBeatsPerMinute(60)
		    .sequenceAndPlay(player, true);
	    System.out.println("Done");
    }

}
