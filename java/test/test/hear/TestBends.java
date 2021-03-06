package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestBends implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBends().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .setBeatsPerMinute(90)
		    .instrument("Halo Pad")
		    .octave(6)
		    .r(4)

		    .p(8, C)
		    .c(4, D_).bend(8, 2).vibrato(8, 64., 8).up()

		    .p(8, B)
		    .octave(5)
		    .p(4, B_, D_-12)

		    .octave(6)
		    .p(8, D_)
		    .p(4, E_, G)

		    .p(8, B-12)
		    .c(8, C).bend(16, 4).bend(16, -4).up()
		    .octave(5)
		    .p(8, E_)
		    .octave(4)
		    .p(8, C)

		    .p(8, B_, B_+3, B_+5)
		    .p(8, B_+2, B_+4, B_+5)
		    .octave(3)
		    .r(8)
		    .p(8, C)

		    .p(8, B_)
		    //.octave(2)
		    .p(8, E_, G, E_+12)
		    .p(8, E_, E_+12)
		    .r(4);
	    midi.play(stop, player);
    }

}
