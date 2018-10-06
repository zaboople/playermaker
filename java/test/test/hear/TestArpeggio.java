package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;


public class TestArpeggio implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestArpeggio().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .setBeatsPerMinute(80)
		    .instrument(1)
		    .bendSense(4)
		    .r(8)
		    .octave(2)
		    .c(1, A)
			    .r(4).up(2., C)
			    .r(2).up(2, G)
			    .r(2.).up(4, F)
			    .up()

		    // This is doing the same as the other two but maybe not "the right way".
			// Also, unrelated, but I am getting vibrato on each by "bendWithParent()".
		    .octave(3)
		    .c(1, A)
			    .r(4).c(2., C).bendWithParent()
				    .r(4).c(2, G).bendWithParent()
					    .r(4).c(4, A-12, C-12, G-12, F).bendWithParent()
						    .up()
					    .up()
				    .up()
			    .vibrato(32, 16)
			    .up()

		    .octave(4)
		    .c(1, A, A-12, C-12, G-12, F-12)
			    .r(4).up(2., C)
			    .r(2).up(2, G)
			    .r(2.).up(4, G-12, F+12)
			    .up()
			.p(16.3, F, A, C, G)
			.p(16.3, F, A, C, G)
			.p(16.3, F, A, C, G)
			.octave(6)
			.c(2., F).vibrato(32, 16).up()

			.r(2)
			;
	    midi.play(stop, player);
    }

}
