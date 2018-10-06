package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;


/**
 * Demonstrates that to play different instruments, we do not need
 * different tracks; we need different channels. Also, our reverb
 * settings are getting us absolutely nothing.
 */
public class TestChannelTrack implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestChannelTrack().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    Player player1=new Player()
		    .setBeatsPerMinute(60)
		    .instrument("Organ - Accordion Fr")
		    .channel(1)
		    .setReverb(0)
		    .octave(5)
		    .r(4)

			.c(4, G).vibrato(64, 8).up()
			.r(4)
			.c(4, E).vibrato(64, 8).up()
			.r(4)

			.r(4)
			.r(4)
			.r(4)
			.octave(3)
			.p(8.3, D+12, D)
			.p(8.3, D_+12, F)
			.c(8., C+12, C).vibrato(64,8).up()
			.octave(4)
			.c(16, C, C+12, C+24).up()
			.c(16, G, G+12, G+24).up()
			.octave(5)
			.c(16, E, E+12, E+24).vibrato(64,8).up()
			.r(32)
			.c(4, E, E+12, E+24).vibrato(64,8).up()
		    .r(4);
	    Player player2=new Player()
		    .instrument("Chiffer Lead")
		    .channel(10)
		    .setReverb(127)
		    .octave(4)
		    .r(4)

			.r(4)
			.r(4)
			.r(4)
			.p(4, A)

			.p(4, G, B, D)
			.r(4)
			.p(4, E, G_, B)

		    .r(4);
	    midi
		    .play(stop, player1, player2);
    }

}
