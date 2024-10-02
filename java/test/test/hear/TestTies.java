package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestTies implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestTies().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player(3)
		    .setBeatsPerMinute(60)
		    .instrument("Strings - Contrabass")
		    .setBendSensitivity(4)
		    .octave(4)
		    .r(4)
		    .c(4, G)
			    .r(8).up(8, A-12, E_)
			    .up()
		    .c(4., A)
			    .r(8).c(4, B_-12, E_).up()
			    .up()
		    .c(16, F).t(16)
			    .r(16).up(16, E_)
			    .up()
		    .c(8, D)
			    .r(16).up(16, C)
			    .up()
		    .c(2, B_)
			    .r(8).c(8, D+12, F).t(4).up()
			    .up()
			.r(4);
	    midi.play(stop, player);
    }

}
