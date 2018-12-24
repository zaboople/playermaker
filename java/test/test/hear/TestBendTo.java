package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;
import org.tmotte.common.text.Log;

public class TestBendTo implements XTest {
    public static void main(String args[]) throws Exception {
        Log.add("BendGen", "MyMidi3", "MidiTracker");
	    new TestBendTo().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player(3)
		    .setBeatsPerMinute(60)
		    .instrument("Strings - Viola")
		    .setBendSensitivity(4)
		    .r(4)

		    .octave(4)

		    // 1
		    .p(8., G)
		    .p(16, A)

		    // 2-3 This has the tricky bend:
		    .p(16.3, C+12)
		    .p(16.3, D_+12)
		    .p(16.3, D+12)
			.c(4., G).bend(8, 2)
				.r(4).c(8, E+12).up()
				.up()

			// 4
			.c(4, F+12).bend(8, 2).up()

			// 5 - And again the tricky bend:
			.c(4, G).bend(8, 2)
				.r(8).c(8, D+12).up()
				.up()

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
		    .r(4);
	    midi.play(stop, player);
    }

}
