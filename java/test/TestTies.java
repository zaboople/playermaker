package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestTies  {
    public static void main(String args[]) throws Exception {
	    Player player=new Player()
		    .instrumentChannel(43, 3)
		    .setBendSensitivity(4)
		    .r4()

		    .octave(4)
		    .c(4, G)
			    .r(8)
			    .c(8, A-12, E_)
			    .up()
		    .c(4, A)
			    .r(8)
			    .c(8, B_-12, E_)
			    .t(8)
			    .up()
		    .c(8, F)
			    .r(16)
			    .c(16, E_)
			    .up()
		    .c(8, D)
			    .r(16)
			    .c(16, C)
			    .up()
		    .c(4, B_)
			    .r(8)
			    .c(8, D+12, F)
			    .t(4)
			    .up()

			.r4();
	    new MyMidi3()
		    .setBeatsPerMinute(60)
		    .playAndStop(player);
	    System.out.println("Done");
    }

}
