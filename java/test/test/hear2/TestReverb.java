package test.hear2;
import org.tmotte.pm2.MyMidi3;
import org.tmotte.pm2.Player;
import static org.tmotte.pm2.Pitches.*;

/* This is actually in 3/8 time. Also tests channel pressure */
public class TestReverb implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestReverb().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
		Player player=new Player()
			.setBeatsPerMinute(65)
		    .instrument(16)
		    .setReverb(100)
		    .setPressure(0)
		    .octave(5)
		    .r(4)

			.p(4, G, B, D)
		    .r(8)
			.p(4, E, G_, B)
		    .r(8)
			.p(4, G, B, D)
		    .r(8)
			.p(4, E, G_, B)
		    .r(8)

		    .setPressure(126)
			.octave(4)
			.p(4, A, D_, E)
		    .r(8)

			.p(16, G_)
			.p(8, E)
			.p(16, G_)
		    .r(8)

			.p(4, A, D_, E)
		    .r(8)

			.p(16, G_)
			.p(8, E)
			.p(16, G_)
			.p(16, A)
			.r(8)

		    .setPressure(0)
		    .octave(5)
			.p(4, G, B, D)
			.p(16.3, B_)
		    .p(16.3, A)
		    .p(16.3, A_)
			.p(4, E, G_, B)
		    .r(8)
			.p(4, G, B, D)
			.r(16.3)
		    .p(16.3, A)
		    .p(16.3, B_)
			.p(4, E, G_, B)
		    .r(8)

		    .setPressure(126)
			.octave(4)
			.p(4, A, D_, E)
		    .r(8)

			.p(16, G_)
			.p(8, E)
			.p(16, G_)
		    .r(8)

			.p(4, A, D_, E)
		    .r(8)

			.p(16, G_)
			.p(8, E)
			.p(16, G_)
			.p(8, A)


		    .r(4)
			;
		midi.play(stop, player);

    }

}
