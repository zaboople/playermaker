package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestVolume implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVolume().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    var bv=20;
	    midi.play(
		    stop,
		    new Player()
			    .setBPM(50)
			    .setBendSensitivity(4)
			    .setPressure(0)
			    .instrument("Strings - Viola")
			    .r4()
			    .octave(5)

			    .c(8., D_, C+12, A_).volume(bv).up()
		        .c(16, D).volume(bv+=4).up()
			    .c(4, E, C+12, E+12)
				    .volume(bv+=4)
				    .up()

			    .c(8., G_, A+12).volume(bv+=8).up()
			    .c(16, E_).volume(bv+=8).up()
			    .c(4, E, D_+12, E+12)
				    .volume(bv+=8)
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
			    .c(4, D)
				    .volume(48)
				    .n(2, E-12).volume(28).up()
				    .n(2, E-24).volume(28).up()
				    .n(2, E-36).volume(28).up()
				    .n(2, E+12).volume(28).up()
				    .r(4).c(4, D_)
				    .up()
			    .c(2, C, E-12, D_-24).volume(30).up()
			    .r(4)
	    );
    }
}
