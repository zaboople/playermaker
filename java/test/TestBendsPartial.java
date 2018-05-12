package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import org.tmotte.pm.Sound;
import org.tmotte.pm.Bend;
import org.tmotte.pm.Divisions;
import static org.tmotte.pm.Pitches.*;

public class TestBendsPartial  {
    public static void main(String args[]) throws Exception {
	    //Organ 1:
	    Player player=new Player()
		    .instrumentTrackChannel(16, 0, 0)
		    .octave(6)
		    .r4()
			.p8(B)
			.r8()
			.p8(D - 24)
			.r8()
			.s2(B).n2(D -24).upup()
			.r8()
			.s2(B).n2(D -24).bend(4, 2).upup()
			.r8()
			.s2(B).n2(D -24).upup()
		    .r4();
	    new MyMidi3()
		    .setBeatsPerMinute(90)
		    .sequenceAndPlay(player);
	    System.out.println("Done");
    }

}
