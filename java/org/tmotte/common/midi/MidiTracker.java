package org.tmotte.common.midi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Optional;

import org.tmotte.common.function.Except;
import javax.sound.midi.*;


/**
 * A general-purpose class for sending Midi Messages to a given Track. It exposes a certain number of high-level
 * messages that I've figured out, like "tell this channel to play this instrument at this time" i.e. sendInstrument(), but it also
 * exposes some lower-level helpers (for no good reason).
 */
public class MidiTracker  {

    private Track currTrack;

    public MidiTracker setTrack(Track t) {
	    currTrack=t;
	    return this;
    }

	public void noteOn(int channel, int pitch, int volume, long currTick) {
        event(channel, ShortMessage.NOTE_ON, pitch, volume, currTick);
    }

	public void noteOff(int channel, int pitch, long currTick) { //FIXME why do I need to send pitch & volume?
        event(channel, ShortMessage.NOTE_OFF, pitch, 0, currTick);
    }


    public void eventBend(int channel, int amount, long tick) {
        // A bend is 14 bits - no, not 16. That won't fit in a byte, but
        // even better, you are required to split it into a 7-bits-each pair.
        int lsb=amount & 127,
            msb=amount >>> 7;
        event(channel, ShortMessage.PITCH_BEND, lsb, msb, tick);
    }
    public void eventBendEnd(int channel, long tick) {
        eventBend(channel, 8192, tick);
    }

	public void sendInstrument(int channel, Instrument instr, long tick) {
		Except.run(()-> {
            Patch patch = instr.getPatch();
			int bank = patch.getBank(),
				program=patch.getProgram();
			int msgType = ShortMessage.CONTROL_CHANGE;
	        sendMessage(
		        new ShortMessage(msgType, channel, 0,  bank >> 7), tick   // = 9
	        );
	        sendMessage(
		        new ShortMessage(msgType, channel, 32, bank & 0x7f), tick // = 0
	        );
	        event(channel, ShortMessage.PROGRAM_CHANGE, program, 0, tick);
        });
	}


    ////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROL CHANGE:                                                                        //
    // https://www.midi.org/specifications/item/table-3-control-change-messages-data-bytes-2  //
    ////////////////////////////////////////////////////////////////////////////////////////////

    /** Short for Bend Sensitivity (hard word to type). */
    public void sendBendSense(int channel, int amount, long tick) {
        //This type of message is command, channel, data1, data2
        //RPN MSB (always 0)
        sendControlChange(channel, 101, 0, tick);
        //RPN LSB (for pitch sensitivity, 0)
        sendControlChange(channel, 100, 0, tick);
        //Data Entry MSB
        sendControlChange(channel, 6, amount, tick);
        //Data Entry LSB:
        sendControlChange(channel, 38, 0, tick);
    }

    public void sendPressure(int channel, int amount, long tick) {
	    event(channel, ShortMessage.CHANNEL_PRESSURE, amount, 0, tick);
    }


    public void sendControlChange(int channel, int data1, int data2, long tick)  {
	    Except.run(()->
	        sendMessage(
	            new ShortMessage(
	                ShortMessage.CONTROL_CHANGE, channel, data1, data2
	            ),
	            tick
	        )
        );
	}

    public void event(int channel, int type, int data1, int data2, long tick) {
        Except.run(()-> {
            ShortMessage message = new ShortMessage();
            message.setMessage(type + channel, data1, data2);
            sendMessage(message, tick);
        });
    }

    public void sendMessage(ShortMessage msg, long tick) {
        currTrack.add(new MidiEvent(msg, tick));
    }


}
