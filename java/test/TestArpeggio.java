package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;
import static org.tmotte.pm.Duration.*;

public class TestArpeggio  {
    public static void main(String args[]) throws Exception {
	    //Organ 1, with double bend-sensitivity:
	    Player player=new Player()
		    .instrument(1)
		    .setBendSensitivity(4)
		    .r8()
		    .octave(2)
		    .c(1, A)
			    .r(4).c(2., C)
			    .r(2).c(2, G)
			    .r(2.).c(4, F)
			    .up()
		    .octave(3)
		    .c(1, A)
			    .r(4).c(2., C)
			    .r(2).c(2, G)
			    .r(2.).c(4, A-12, C-12, G-12, F)
			    .vibrato(32, 16)
			    .up()
		    .octave(4)
		    .c(1, A, A-12, C-12, G-12, F-12)
			    .r(4).c(2., C)
			    .r(2).c(2, G)
			    .r(2.).c(4, G-12, F+12)
			    .up()
			.p(16.3, F, A, C, G)
			.p(16.3, F, A, C, G)
			.p(16.3, F, A, C, G)
			.octave(6)
			.c(2., F).vibrato(32, 16).up()


			//1
			.r2()
			;
	    new MyMidi3()
		    .setBeatsPerMinute(80)
		    .sequenceAndPlay(player, true);
	    System.out.println("Done");
    }

}
