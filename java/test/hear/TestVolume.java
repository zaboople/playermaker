package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestVolume implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVolume().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
		int checks=15;
	    midi.play(stop,
		    new Player()
			    .setBPM(50)
			    .setBendSensitivity(4)
			    .setPressure(0)
			    .instrument("Strings - Viola")
			    .r4()
			    .octave(5)

			    .c(8., D_).volume(12).up()
		        .c(16, D).volume(16).up()
			    .c(4, E)
				    .volume(24)
				    .up()

			    .c(8., G_).volume(36).up()
			    .c(16, E_).volume(42).up()
			    .c(4, E)
				    .volume(48)
				    .up()

				// Note how here it sounds like a pitch change
				// even though the notes are the same; it's just
				// that I'm using different volumes:
			    .c(4)
				    .n(4, F).volume(20).up()
				    .n(4, G).volume(48).up()
				    .up()
			    .c(4)
				    .n(4, F).volume(48).up()
				    .n(4, G).volume(20).up()
				    .up()
			    .r(4)
	    );
    }
}
