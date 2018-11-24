package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.common.text.Log;
import static org.tmotte.pm.Pitches.*;

public class TestBendTie implements XTest {
    public static void main(String args[]) throws Exception {
        Log.add("BendGen", "MyMidi3", "Bend", "MidiTracker");
	    new TestBendTie().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .setBeatsPerMinute(60)
		    .instrument("Soprano Sax")
		    .reverb(64)
		    .bendSense(4)
		    .r(8)
			    .octave(3)
			    .p(8.3, A, C+12, A+12)
			    .p(8.3, B_, D_+12, B_+12)
			    .p(8.3, B, D+12, B+12)

			    .octave(3)
			    .c(8.3, A, E+12)
				    .bend(2)
				    .t(8.3).t(8.3)
				    .vibrato(64, 16)
				    .up()

				.octave(3)
			    .c(4, A, G_+12)
				    .tag("HELLO")
				    .bend(16, 2)
				    .vibrato(8, 64, 16)
				    .bend(16, -2)
				    .t(8)
				    .vibrato(64, 16)//FIXME the vibrato leaves us out of tune, and so the -4 is also out of tune
				    .t(8)
				    .bend(-2)
				    .up()


			    .p(8.3, B_, D+12)
			    .p(8.3, A, C+12)
			    .c(4, B, D+12, G+12).swell(8., 16, 10).vibrato(64, 16)
				    .up()

		    .r(8)
			;
	    midi.play(stop, player);
    }

}
