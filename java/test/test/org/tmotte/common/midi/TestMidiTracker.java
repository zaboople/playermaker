package org.tmotte.common.midi;
import java.util.Optional;
import javax.sound.midi.Sequence;
import org.tmotte.common.midi.MidiTracker;
import org.tmotte.common.midi.SequencerMgr;
import org.tmotte.common.text.Log;

public class TestMidiTracker {
    public static void main(String args[]) throws Exception {
	    Log.add("MidiTracker");

	    //FIXME maybe violin will make the slur/portamento work?
	    try (SequencerMgr mgr=new SequencerMgr()) {
		    Sequence sequence=new Sequence(Sequence.PPQ, 15);
		    MidiTracker mt=new MidiTracker(sequence.createTrack());
		    mt.sendInstrument(0, mgr.getInstrument("Strings - Viola"), 0);
		    mt.noteOn(0, 24+22, 127, 10);
		    mt.noteOn(0, 24+29, 127, 60);
		    mt.noteOff(0, 24+22, 90);
		    mt.noteOff(0, 24+29, 120);

			mt.sendPortamentoTime(0, 64, 0);
		    mt.noteOn(0, 24+22, 127, 130);
		    mt.sendPortamento(0, 24+22, 131);
		    mt.noteOff(0, 24+22, 160);
		    mt.noteOn(0, 24+29, 127, 160);
		    mt.noteOff(0, 24+29, 200);
		    mt.sendPortamentoOff(0, 201);
		    mgr.play(sequence);
	    }
	    System.out.println("Done");
    }

}
