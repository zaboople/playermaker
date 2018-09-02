package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestVolume implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestVolume().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    //FIXME BROKEN
		int checks=15;
	    midi.play(stop,
		    new Player()
			    .setBPM(50)
			    .setBendSensitivity(4)
			    .setPressure(0)
			    .instrument("Strings - Viola")
			    .r4()
			    .octave(5)
			    .c(4, D_)
				    .volume(127)
				    .up()
			    .c(4, E)
				    .volume(12)
				    .up()
			    .c(4, G_)
				    .volume(12)
				    .up()
			    .r(4)
	    );
    }
}
