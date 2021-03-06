package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;

public class TestChords implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestChords().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
		int checks=15;
	    Player player=new Player()
		    .setBPM(90)
		    .r(4);
		for (int i=0; i<checks; i++)
		    player
			    .octave(2 + (i % 3))
			    .p(16, i+1, 13, 25);
	    player
		    .instrument("Strings - Cello")
		    .p(8., checks, checks+5, checks+8)
		    .p(4, checks-1, checks-1+4, checks-1+7)
		    .p(8, checks-2, checks+3, checks+5)
		    .p(4., checks-4, checks, checks+3)
		    .instrument("Piano - Piano 2")
		    .p(1, checks-5, checks-3, checks)
		    .r(4);
	    midi.play(stop, player);
    }
}
