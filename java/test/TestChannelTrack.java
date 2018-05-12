package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;

/**
 * Demonstrates that to play different instruments, we do not need
 * different tracks; we need different channels. Also, our reverb
 * settings are getting us absolutely nothing.
 */
public class TestChannelTrack  {
    public static void main(String args[]) throws Exception {
	    //Accordion:
	    Player player1=new Player()
		    .instrumentTrackChannel(21, 0, 1)
		    .setReverb(0)
		    .octave(5)
		    .r4()
			.p4(G)
			.r4()
			.p4(E)
		    .r2();
	    //Piano:
	    Player player2=new Player()
		    .instrumentTrackChannel(0, 0, 10)
		    .setReverb(127)
		    .octave(4)
		    .r4()
			.r4()
			.r4()
			.r4()
			.r4()
			.p4(G, B, D)
			.r4()
			.p4(E, G_, B)
		    .r2();
	    new MyMidi3()
		    .setBeatsPerMinute(60)
		    .sequence(player1, player2)
		    .play(true);
    }

}
