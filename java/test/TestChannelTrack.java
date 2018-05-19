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
		    .instrumentChannel(21, 1)
		    .setReverb(0)
		    .octave(5)
		    .r4()

			.s4(G).vibrato(64, 8).up()
			.r4()
			.s4(E).vibrato(64, 8).up()
			.r4()

			.r4()
			.r4()
			.r4()
			.octave(3)
			.p(8.3, D+12, D)
			.p(8.3, D_+12, F)
			.c(8., C+12, C).vibrato(64,8).up()
			.octave(4)
			.c(16, C, C+12, C+24).up()
			.c(16, G, G+12, G+24).up()
			.octave(5)
			.c(16, E, E+12, E+24).vibrato(64,8).up()
			.r32()
			.c(4, E, E+12, E+24).vibrato(64,8).up()
		    .r4();
	    //Chiffer Lead:
	    Player player2=new Player()
		    .instrumentChannel(83, 10)
		    .setReverb(127)
		    .octave(4)
		    .r4()

			.r4()
			.r4()
			.r4()
			.p4(A)

			.p4(G, B, D)
			.r4()
			.p4(E, G_, B)

		    .r4();
	    new MyMidi3()
		    .setBeatsPerMinute(60)
		    .sequence(player1, player2)
		    .play(true);
    }

}
