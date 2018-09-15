package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

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
		    .r4()

			.p4(G, B, D)
		    .r8()
			.p4(E, G_, B)
		    .r8()
			.p4(G, B, D)
		    .r8()
			.p4(E, G_, B)
		    .r8()

		    .setPressure(126)
			.octave(4)
			.p4(A, D_, E)
		    .r8()

			.p16(G_)
			.p8(E)
			.p16(G_)
		    .r8()

			.p4(A, D_, E)
		    .r8()

			.p16(G_)
			.p8(E)
			.p16(G_)
			.p16(A)
			.r8()

		    .setPressure(0)
		    .octave(5)
			.p4(G, B, D)
			.p(16.3, B_)
		    .p(16.3, A)
		    .p(16.3, A_)
			.p4(E, G_, B)
		    .r8()
			.p4(G, B, D)
			.r(16.3)
		    .p(16.3, A)
		    .p(16.3, B_)
			.p4(E, G_, B)
		    .r8()

		    .setPressure(126)
			.octave(4)
			.p4(A, D_, E)
		    .r8()

			.p16(G_)
			.p8(E)
			.p16(G_)
		    .r8()

			.p4(A, D_, E)
		    .r8()

			.p16(G_)
			.p8(E)
			.p16(G_)
			.p8(A)


		    .r4()
			;
		midi.play(stop, player);

    }

}