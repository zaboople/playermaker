package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;
import static org.tmotte.pm.Tie.tie;

public class TestTies2 implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestTies2().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player(3)
		    .setBeatsPerMinute(60)
		    .instrument("Trombone 2")
		    .setBendSensitivity(4)
		    .r(4)

		    .octave(4)
		    .p(tie(16., 32.), D, G_)
		    .p(tie(8., 16.), C, G_)

		    .p(tie(16., 32.), E, G_)
		    .p(tie(8., 16.), B, G_)

		    .p(tie(16., 32.), E, G_)
		    .p(tie(8., 16.), B, E)

		    .p(tie(16., 32.), A_, C+12)
		    .p(tie(8., 16.), A, C+12, E, F-12)

		    .p(tie(16., 32.), E, C)

		    .p(16., B)
		    .p(32., A)
		    .p(tie(16., 32.), E)

		    .p(16., B)
		    .p(32., A)
		    .p(tie(16., 32.), D)

		    .p(16., B)
		    .p(32., A)
		    .p(tie(16., 32., 2), E, B-12, D-24)
			.r(4);
	    midi.play(stop, player);
    }

}
