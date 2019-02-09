package org.tmotte.common.midi;
import java.util.Optional;
import javax.sound.midi.Sequence;
import org.tmotte.common.midi.MidiTracker;
import org.tmotte.common.midi.SequencerMgr;

public class TestMidiTracker {
    public static void main(String args[]) throws Exception {
	    try (SequencerMgr mgr=new SequencerMgr()) {
		    Sequence sequence=new Sequence(Sequence.PPQ, 15);
		    MidiTracker mt=new MidiTracker(sequence.createTrack());
		    mt.noteOn(0, 22, 127, 0);
		    mt.noteOn(0, 28, 127, 20);
		    mt.noteOff(0, 28, 100);
		    mt.noteOff(0, 22, 120);
		    mgr.play(sequence);
	    }
	    System.out.println("Done");
    }

}
