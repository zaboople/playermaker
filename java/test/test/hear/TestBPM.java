package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

/** FIXME */
public class TestBPM implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBPM().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .instrument("Strings - Contrabass")
		    .channel(0)
		    .setBendSensitivity(4)
		    .octave(4)
		    .r(4)

		    .bpm(30)
		    .p(16.3, G)
		    .p(16.3, A_)
		    .p(16.3, A)
		    .p(8, G, A)

		    .bpm(30)
		    .c(16., G-12)
			    .r(32.3)
				    .finup(C)
			    .up()
		    .r(32.3).r(32.3)
		    .p(16.3, D)
		    .p(8, G, D)


		    .p(8, D+24)


		    .bpm(30)
		    .c(16.3, G)
			    .r(16.3).up(16.3, A)
			    .up()
		    .p(16.3, B_)

		    .bpm(30)
		    .c(16.3, G)
			    .r(16.3).up(16.3, A)
			    .up()
		    .p(16.3, B_)

		    .bpm(40)
		    .p(16.3, C)
		    .p(16.3, D_)
		    .p(16.3, E)
		    .bpm(50)
		    .p(16.3, G)
		    .p(16.3, A)
		    .p(16.3, B_)
		    .bpm(60)
		    .p(16.3, F)
		    .p(16.3, E_)
		    .p(16.3, G)

		    .bpm(70)
		    .p(16.3, E_)
		    .p(16.3, G)
		    .p(16.3, B)
		    .p(8, E_, G, B)
		    .bpm(60)
		    .p(16.3, C)
		    .p(16.3, D_)
		    .p(16.3, E)
		    .p(8, C, E)
		    .bpm(50)
		    .p(16.3, E)
		    .p(16.3, A_)
		    .p(16.3, B)
		    .p(8, E, A_, B)
		    .bpm(40)
		    .p(16.3, C)
		    .p(16.3, D_)
		    .p(16.3, G)
		    .p(2, B, E-12, C, G)

			.r(4);
	    midi.play(stop, player);
    }

}
