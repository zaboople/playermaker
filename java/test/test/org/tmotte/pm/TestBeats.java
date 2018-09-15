package org.tmotte.pm;

/**
 * The way things are done now, there is some loss of information when calculating
 * odd BPM's, because we arrive at a constant factor via rounding.
 */
public class TestBeats  {
    public static void main(String args[]) throws Exception {
		TestBeats tb=new TestBeats();
	    tb.test(60);
	    tb.test(30);
	    tb.test(120);
	    tb.test(100);
	    tb.mustPass=false;
	    tb.test(47);
	    tb.test(132);
    }

    MyMidi3 midi=new MyMidi3();
    boolean mustPass=true;

    private void test(int bpm) {
	    System.out.println("\nTest BPM "+bpm);
	    midi.setBeatsPerMinute(bpm);

	    long ticksPerBeat=Math.round(
		    ((double)MyMidi3.TICKS_PER_MINUTE) /
		    ((double)bpm)
	    );
	    System.out.println("Resolution "+MyMidi3.SEQUENCE_RESOLUTION);
	    System.out.println("Ticks per beat "+ticksPerBeat);
	    long ticksPer4 = ticksPerBeat;
	    long ticksPer8 = ticksPer4 / 2;
	    long ticksPer16 = ticksPer8 / 2;
	    long ticksPer64 = ticksPer16 / 4;
	    verify("Quarter", ticksPerBeat, Divisions.reg4);
	    verify("Eighth", ticksPerBeat /2 , Divisions.reg8);
	    verify("16th", ticksPerBeat /4 , Divisions.reg16);
	    verify("32nd", ticksPerBeat /8 , Divisions.reg32);
	    verify("64th", ticksPerBeat /16 , Divisions.reg64);
    }

    private void verify(String name, long shouldBe, long division) {
	    System.out.append(name).append(": ");
	    long check=division * midi.tickX;
	    System.out.append(shouldBe+" == "+check).append("  ...?\n");
	    if (mustPass && shouldBe!=check)
		    throw new RuntimeException("Not a match");
    }

}
