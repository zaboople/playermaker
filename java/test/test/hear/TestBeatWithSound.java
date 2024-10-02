package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

/** This should play 2 rolls per second. */
public class TestBeatWithSound implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBeatWithSound().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    final int bpm = 14;
	    Player player=new Player(1)
		    .instrument(midi.findInstrument("Viola"))
		    .setBeatsPerMinute(bpm)
		    .octave(2);
	    Player drummer=Player.drummer()
		    .instrument(midi.findInstrument("Drum Orchestra"))
		    .volume(110)
		    .setBeatsPerMinute(bpm)
		    .octave(2);
		int checks=3;
		for (int i=0; i<checks; i++) {
		    player.c(4, 24+i)
			    .r(16).c(8, 28+i).up()
			    .r(8).c(16, 31+i).up()
			    .r(8.).c(16, 31+i);
		    drummer.p(16, E).r(16).p(16, E+12).r(16);
	    }
	    midi.sequence(player, drummer);
	    midi.play(stop);
    }

}
