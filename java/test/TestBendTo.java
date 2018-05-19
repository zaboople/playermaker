package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;

public class TestBendTo  {
    public static void main(String args[]) throws Exception {
	    Player player=new Player()
		    .instrumentChannel(41, 3)
		    .setBendSensitivity(4)
		    .r4()

		    .octave(4)

		    // 1
		    .p(8., G)
		    .p(16, A)

		    // 2-3 This has the tricky bend:
		    .p(16.3, C+12)
		    .p(16.3, D_+12)
		    .p(16.3, D+12)
			.n(4., G).bend(8, 2).up()
				.r(4).n(8, E+12).upup()

			// 4
			.c(4, F+12).bend(8, 2).up()

			// 5 - And again the tricky bend:
			.n(4, G).bend(8, 2).up()
				.r(8)
				.n(8, D+12).upup()
			// 6
			.c(4, E+12+2, G+12+2)
				.bend(8, -2).up()

			// 7
		    .p(8., G)
		    .p(16, A)

			// 8-9
			.p(16.3, C, E)
			.p(16.3, G, B)
			.p(16.3, D, B_)
			.p(4., A, A-12, A+12)
		    .r4();
	    new MyMidi3()
		    .setBeatsPerMinute(60)
		    .sequenceAndPlay(player, true);
	    System.out.println("Done");
    }

}
