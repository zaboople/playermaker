package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestVibratoSpeed  {
    public static void main(String args[]) throws Exception {
	    //Organ 1, with double bend-sensitivity:
	    Player player=new Player()
		    .instrumentChannel(0, 0)
		    .setBendSensitivity(4)
		    .volume(127)
		    .octave(5)
		    .r4()

			.c(1, F-12, C, G)
				.bend(4, 2)
				.vibrato(2., 64, 12).up()

			.c(2., F-12, C, G)
				.vibrato(64, 12).up()

			.c(1, F-12, C, G)
				.bend(4, 2)
				.vibrato(64, 12).up()

		    /*
		    .c(1, C)
			    .bend(4, 2)
			    .vibrato(64, 4)
			    .up()
		    .c(2, D)
			    .bend(8, 2)
			    .vibrato(64, 4)
			    .up()
		    */
		    .r4();
	    new MyMidi3()
		    .setBeatsPerMinute(60)
		    .playAndStop(player);
	    System.out.println("Done");
    }

}
