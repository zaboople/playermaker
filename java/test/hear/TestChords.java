package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;

public class TestChords implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestChords().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
		int checks=15;
	    Player player=new Player()
		    .r4();
		for (int i=0; i<checks; i++)
		    player
			    .octave(2 + (i % 3))
			    .p16(i+1, 13, 25);
	    player
		    .instrument(42)
		    .p8(checks, checks+5, checks+8)
		    .p4(checks-1, checks-1+4, checks-1+7)
		    .p8(checks-2, checks+3, checks+5)
		    .p(4, checks-4, checks, checks+3)
		    .instrument(1)
		    .p(1, checks-5, checks-3, checks)
		    .r4();
	    midi.setBeatsPerMinute(90).play(stop, player);
    }
}
