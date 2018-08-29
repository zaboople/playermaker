package test.hear;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;

/** Look in java System properties for sound values. */
public class TestBeatWithSound implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBeatWithSound().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    midi.setBeatsPerMinute(60);
	    Player player=new Player().octave(3);
		int checks=10;
		for (int i=0; i<checks; i++)
		    player.c(4, 24+i).c(8, 28+i).c(16, 31+i).up();
	    midi.sequence(player);
	    doTimerThread(checks);
	    midi.play(stop);
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
