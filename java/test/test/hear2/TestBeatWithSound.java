package test.hear2;
import org.tmotte.pm2.MyMidi3;
import org.tmotte.pm2.Player;

/** This should play 2 rolls per second. */
public class TestBeatWithSound implements XTest {
    public static void main(String args[]) throws Exception {
	    new TestBeatWithSound().test(new MyMidi3(), true);
    }
    public @Override void test(MyMidi3 midi, boolean stop)  {
	    Player player=new Player()
		    .setBeatsPerMinute(120)
		    .octave(2);
		int checks=20;
		for (int i=0; i<checks; i++)
		    player.c(4, 24+i)
			    .r(16).c(8, 28+i).up()
			    .r(8).c(16, 31+i).up()
			    .r(8.).c(16, 31+i);
	    midi.sequence(player);
	    doTimerThread(checks);
	    midi.play(stop);
    }
    private static void doTimerThread(int checks) {
	    new Thread() {
		    public void run() {
			    long before=System.currentTimeMillis();
			    for (int i=0; i<10+2; i++) {
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
