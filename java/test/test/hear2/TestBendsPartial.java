package test.hear2;
import org.tmotte.pm2.MyMidi3;
import org.tmotte.pm2.Player;
import static org.tmotte.pm2.Pitches.*;

public class TestBendsPartial implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBendsPartial().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    //Organ 1:
	    Player player=new Player()
		    .setBeatsPerMinute(90)
		    .instrument(16)
		    .octave(5)

		    .r(4)

			// First let's play two notes separate, then together:
			.p(8, B)
			.p(8, D-12)
			.r(32)
			.p(4, D-12, B)
			.r(32)

			// Then let's play them together again, bending one:
			.c(2, D-12).bend(4, 2)
				.up(2, B)
				.up()
			.r(32)

			// Let's play what should be the same pitch as the bent
			// combination, followed by the original
			.p(2, D-11, B)
			.r(32)
			.p(2, D-12, B)


			// And I don't know, something that sounds okay:
			.octave(5)

			// 1
			.p(4, A, E)

			// 2
			.c(8., E).bend(1)
				.up(8., A)
				.up()
			.p(32, E)
			.p(32, F)

			// 3 + 4
			.c(2, G_)
				.r(8).up(4., A)
				.up()

			// 5 & 1/2
			.p(8, F)
			.c(8., E)
				.r(16).up(8, A)
				.up()
			.p(16, E, G)

			// 6
			.p(8, D, G)

			// 7 & 8
			.p(2, F-12, A-12, C, G)

		    .r(4);
	    midi
		    .play(stop, player);
	    System.out.println("Done");
    }

}
