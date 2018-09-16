package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestBPM implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBPM().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .instrument("Strings - Contrabass")
		    .channel(0)
		    .setBendSensitivity(4)
		    .octave(3)
		    .r4()


		    .bpm(30)
		    .p(16.3, G)
		    .p(16.3, A_)
		    .p(16.3, A)

		    .bpm(30)
		    .c(16, G-12)
		    .r(16.3)
			    .c(16.3, C).t(16.3)
		    .r(16.3).t(16.3)
			    .c(16.3, D)
			    .t(2)
			    .up()

		    .p(D+24, 4)
		    .volume(0)


		    .bpm(30)
		    .c(16.3, G)
		    .r(16.3)
			    .c(16.3, A)
		    .p(16.3, B_)

		    .bpm(30)
		    .c(16.3, G)
		    .r(16.3)
			    .c(16.3, A)
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
		    .bpm(60)
		    .p(16.3, C)
		    .p(16.3, D_)
		    .p(16.3, E)
		    .bpm(50)
		    .p(16.3, E_)
		    .p(16.3, G)
		    .p(16.3, B)
		    .bpm(40)
		    .p(16.3, C)
		    .p(16.3, D_)
		    .p(16.3, G)

			.r4();
	    midi.play(stop, player);
    }

}
