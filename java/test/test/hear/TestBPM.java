package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;
import org.tmotte.common.text.Log;

/** FIXME */
public class TestBPM implements XTest {
    public static void main(String args[]) throws Exception {
        Log.add("SwellGen", "MyMidi3" /*, "MidiTracker"*/);
	    new TestBPM().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .instrument("Organ - Bandoneon")
		    .channel(0)
		    .setBendSensitivity(4)
		    .octave(4)
		    .pressure(64)
		    .r(4)

		    .bpm(20)
		    .p(16.3, G)
		    .p(16.3, A_)
		    .p(16.3, A)
		    .p(8, G, A)

		    .bpm(25)
		    .c(4, G-12)
			    .r(16.3)
				    .finup(C)
			    .r(16.3).t(16.3)
				    .finup(E)
			    .up()

		    .bpm(35)
		    .c(4, G)
			    .r(16.3)
				    .finup(A)
			    .r(16.3).t(16.3)
				    .finup(D_)
			    .up()

		    .bpm(40)
		    .c(4, G-12)
			    .r(16.3)
				    .finup(B)
			    .r(16.3).t(16.3)
				    .finup(F)
			    .up()

		    .bpm(50)
		    .p(16.3, C)
		    .p(16.3, D_)
		    .p(16.3, E)
		    .bpm(60)
		    .p(16.3, G)
		    .p(16.3, A)
		    .p(16.3, B_)
		    .bpm(70)
		    .p(16.3, F)
		    .p(16.3, E_)
		    .p(16.3, G)
		    .bpm(70)
		    .p(16.3, F)
		    .p(16.3, E_)
		    .p(16.3, G)

		    .c(2, B+24)
			    .r(8).finup(D+24)
			    .r(4).finup(C+12)
			    .up()

		    .bpm(60)
		    .p(16.3, E_)
		    .p(16.3, G)
		    .p(16.3, B)
		    .p(8, E_, G, B)
		    .bpm(50)
		    .p(16.3, C)
		    .p(16.3, D_)
		    .p(16.3, E)
		    .p(8, C, E)
		    .bpm(40)
		    .p(16.3, E)
		    .p(16.3, A_)
		    .p(16.3, B)
		    .p(8, E, G, B)
		    .bpm(30)
		    .p(16.3, C)
		    .p(16.3, D_)
		    .p(16.3, G)
		    .p(4, B, E-12, C, G)

			.r(4);
	    midi.play(stop, player);
    }

}
