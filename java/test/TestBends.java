package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

public class TestBends  {
    public static void main(String args[]) throws Exception {
	    //Organ 1:
	    Player player=new Player().instrumentTrackChannel(16, 0, 0);
		int base=10;
	    player
		    .octave(6)
		    .r4()

		    .p8(C)
		    .s4(D_).bend(16, 2).up()

		    .p8(B)
		    .octave(5)
		    .p4(B_, D_-12)

		    .octave(6)
		    .p8(D_)
		    .p4(E_, G)

		    .p8(B-12)
		    .s8(C).bend(16, 4).bend(16, -4).up()
		    .octave(5)
		    .p8(E_)
		    .octave(4)
		    .p8(C)

		    .p8(B_, B_+3, B_+5)
		    .p8(B_+2, B_+4, B_+5)
		    .octave(2)
		    .r8()
		    .p8(C)

			.volume(100)
		    .p8(B_)
		    .octave(1)
		    .p8(E_, G, E_+12)
		    .p16(E_, E_+12)
		    .r4();
	    new MyMidi3()
		    .setBeatsPerMinute(90)
		    .sequenceAndPlay(player, true);
	    System.out.println("Done");
    }

}
