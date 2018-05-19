package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;

/* This is actually in 3/8 time. No, the reverb is still broken :{ */
public class TestReverb  {
    public static void main(String args[]) throws Exception {
	    new MyMidi3()
		    .setBeatsPerMinute(65)
		    .sequenceAndPlay(make(), true);
	    System.out.println("Done");
    }
    private static Player make() {
		return new Player()
		    .instrumentTrackChannel(16, 0, 0)
		    .setReverb(0)
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
    }

}
