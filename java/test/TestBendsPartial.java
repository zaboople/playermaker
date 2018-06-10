package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestBendsPartial implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBendsPartial().test(new MyMidi3(), true);
    }
    public void test(MyMidi3 midi, boolean stop)  {
	    //Organ 1:
	    Player player=new Player()
		    .instrumentTrackChannel(16, 0, 0)
		    .octave(6)
		    .r(4)
			.p(8, B)
			.r(8)
			.p(8, D - 24)
			.r(8)
			.c(2, B)
				.n(2, D -24).upup()
			.r(8)
			.c(2, B)
				.n(2, D -24).bend(4, 2).upup()
			.r(8)
			.c(2, B)
				.n(2, D -23).upup()
		    .r4();
	    midi
		    .setBeatsPerMinute(90)
		    .play(stop, player);
	    System.out.println("Done");
    }

}
