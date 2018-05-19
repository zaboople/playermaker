package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;

public class TestBendTo  {
    public static void main(String args[]) throws Exception {
	    //Organ 1, with double bend-sensitivity:
		System.out.println(48+G);
		System.out.println(48+A);
		System.out.println(48+C+12);
		System.out.println(48+D_+12);
		System.out.println(48+D+12);
	    Player player=new Player()
		    .instrumentChannel(41, 3)
		    .setBendSensitivity(4)
		    .r4()

		    .octave(4)
		    /*
		    .p(4, G)
		    .p(4, A)
		    .p(4, C+12)
		    .p(4, D_+12)
		    .p(4, D+12)
		    */

			.c(8, G)
				.bend(2)
				.up()
			.p(8, D+12)

			.c(4, G)
				.bend(8, 2)
				.up()
			.p(8, D+12)

			//.p(8, G+12)


			/** FIXME THIS IS BENDING BOTH NOTES! **/
			.n(4, G).bend(8, 2).up()
				.r(8).n(8, D+12).upup()
			.p(8, A)
		    .r4();
	    new MyMidi3()
		    .setBeatsPerMinute(50)
		    .sequenceAndPlay(player, true);
	    System.out.println("Done");
    }

}
