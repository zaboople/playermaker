package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;

public class TestReverb  {
    public static void main(String args[]) throws Exception {
		{
		    Player player=new Player()
			    .instrumentTrackChannel(16, 0, 0)
			    .setReverb(127)
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
			    .r2()
				;
			    new MyMidi3()
				    .setBeatsPerMinute(60)
				    .sequenceAndPlay(player, true);

	    }
	    System.out.println("Done");
    }

}
