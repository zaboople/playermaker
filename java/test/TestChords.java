package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;

public class TestChords  {
    public static void main(String args[]) throws Exception {
	    MyMidi3 midi=new MyMidi3();
	    midi.setBeatsPerMinute(90);
	    Player player=new Player();
		int checks=15;
	    player.r4();
		for (int i=0; i<checks; i++)
		    player
			    .octave(2 + (i % 3))
			    .p16(i+1, 13, 25);
	    player.p8(checks, checks+5, checks+8)
		    .p2(checks-1, checks-1+4, checks-1+7)
		    .r4();
	    midi.sequenceAndPlay(player, true);
    }

}
