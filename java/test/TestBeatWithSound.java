package test;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;

/** Look in java System properties for sound values. */
public class TestBeatWithSound  {
    public static void main(String args[]) throws Exception {
	    MyMidi3 midi=new MyMidi3();
	    midi.setBeatsPerMinute(60);
	    Player player=new Player();
		int checks=10;
		for (int i=0; i<checks; i++)
		    player.p4(24+i);
	    midi.sequence(player);
	    doTimerThread(checks);
	    midi.play(true);
    }
    private static void doTimerThread(int checks) {
	    new Thread() {
		    public void run() {
			    long before=System.currentTimeMillis();
			    for (int i=0; i<checks+2; i++) {
				    for (int j=0; j<10; j++) {
					    try {
						    Thread.sleep(100);
						    System.out.print(".");
					    } catch (Exception e) {
					    }
				    }
				    System.out.println("\t"+i+"\t"+(System.currentTimeMillis()-before));
			    }
		    }
	    }.start();
    }

}
