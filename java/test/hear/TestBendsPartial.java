package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

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


			.p(8, B)
			.p(8, D - 12)
			.r(32)

			.p(4, B, D -12)
			.r(32)

			.c(2, B)
				.n(2, D -12).bend(4, 2).upup()
			.r(32)

			.c(2, B)
				.n(2, D -11).upup()
			.r(32)
			.c(2, B)
				.n(2, D -12).upup()


			.octave(5)

			// 1
			.p(4, A, E)

			// 2
			.c(8., A)
				.n(8., E)
				.bend(1)
				.upup()
			.p(32, E)
			.p(32, F)

			// 3 + 4
			.c(2, G_)
				.r(8).n(4., A).upup()

			// 5 & 1/2
			.p(8, F)
			.c(8., E).r(16).n(8, A).upup()
			.p(16, E, G)

			// 6
			.p(8, D, G)

			// 7 & 8
			.p(2, F-12, A-12, C, G)

		    .r4();
	    midi
		    .play(stop, player);
	    System.out.println("Done");
    }

}
