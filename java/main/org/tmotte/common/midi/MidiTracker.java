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
import org.tmotte.common.text.Log;
import javax.sound.midi.*;


/**
 * A general-purpose class for sending Midi Messages to a given Track. Essentially this provides a lot of sequencing
 * methods that the Java Midi API should already have, but "forgot" to include. A lot of these were quite difficult to
 * figure out.
 */
public class MidiTracker  {

    private Track currTrack;

    public MidiTracker() {}

    public MidiTracker(Track t) {
        this.currTrack=t;
    }

    public MidiTracker createTrack(Sequence sequence) {
        return setTrack(sequence.createTrack());
    }

    public MidiTracker setTrack(Track t) {
        currTrack=t;
        return this;
    }

    public void noteOn(int channel, int pitch, int volume, long tick) {
        // NOTE_ON=144
        Log.log("MidiTracker", "noteOn() tick {} pitch {} volume {}", tick, pitch, volume);
        event(channel, ShortMessage.NOTE_ON, pitch, volume, tick);
    }

    public void noteOff(int channel, int pitch, long tick) {
        event(channel, ShortMessage.NOTE_OFF, pitch, 0, tick);
    }


    public void sendInstrument(int channel, Instrument instr, long tick) {
        Patch patch=instr.getPatch();
        int bank   =patch.getBank();
        sendControlChange(channel, 0,  getMSB(bank), tick);
        sendControlChange(channel, 32, getLSB(bank), tick);
        event(channel, ShortMessage.PROGRAM_CHANGE, patch.getProgram(), 0, tick);
    }


    /** Short for Bend Sensitivity (hard word to type).
        @param channel Channel
        @param amount Amount of bend sensitivity
        @param tick Time to send message
    */
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
    public void sendBend(int channel, int amount, long tick) {
        Log.log("MidiTracker", "Bend {} at {} ", amount, tick);
        // A bend is 14 bits - no, not 16. That won't fit in a byte, but
        // even better, you are required to split it into a 7-bits-each pair.
        event(channel, ShortMessage.PITCH_BEND, getLSB(amount), getMSB(amount), tick);
    }
    public void sendBendEnd(int channel, long tick) {
        sendBend(channel, 8192, tick);
    }

    /** This is vibrato (usually)
        @param channel Channel
        @param amount Amount of vibrato "pressure"
        @param tick Time to send message
    */
    public void sendPressure(int channel, int amount, long tick) {
        event(channel, ShortMessage.CHANNEL_PRESSURE, amount, 0, tick);
    }

    /** This is volume swells
        @param channel Channel
        @param volume Volume level
        @param tick Time to send message
    */
    public void sendExpression(int channel, int volume, long tick) {
        Log.log("MidiTracker", "sendExpression() tick {} volume {}", tick, volume);
        sendControlChange(channel, 11, volume,  tick);
    }



    /**
     * Portamento time appears to be limited to 0-127, but then we're allowed to
     * send a MSB and LSB and so I'm not sure. And it looks like the Java synthesizer
     * doesn't handle portamento at all, not to mention that there's ambiguity about
     * polyphony/chords to begin with.
     *
     * @param channel Channel
     * @param amount 0-127 or 0-4095 (14 bits)
     * @param tick Time to send message
     */
    public void sendPortamentoTime(int channel, int amount, long tick) {
        Log.log("MidiTracker", "sendPortamentoTime() tick {} amount {}", tick, amount);
        sendControlChange(channel, 5, getMSB(amount),  tick);
        sendControlChange(channel, 37, getLSB(amount),  tick);
    }

    /**
     * Again, I never got portamento verified with Java's synthesizer.
     *
     * @param channel Channel
     * @param noteFrom The note to (sorta) "slur" from when the next note-on
     * message is received.
     * @param tick Time to send message
     */
    public void sendPortamento(int channel, int noteFrom, long tick) {
        Log.log("MidiTracker", "sendPortamento() tick {} noteFrom {}", tick, noteFrom);
        // Portamento on, then send the note to "slur" from:
        sendControlChange(channel, 65, 64,  tick);
        sendControlChange(channel, 84, noteFrom,  tick);
    }

    public void sendPortamentoOff(int channel, long tick) {
        Log.log("MidiTracker", "sendPortamentoOff() tick {} ", tick);
        sendControlChange(channel, 65, 0,  tick);
    }



    ////////////////
    // INTERNALS: //
    ////////////////

    /** Get the most significant 7 - not 8 - bits */
    private int getMSB(int value) {
        return value >>> 7;
    }

    /** Get the least significant 7 bits */
    private int getLSB(int value) {
        return value & 127; // 127 is binary 0111 1111
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROL CHANGE:                                                                        //
    // https://www.midi.org/specifications/item/table-3-control-change-messages-data-bytes-2  //
    ////////////////////////////////////////////////////////////////////////////////////////////

    private void sendControlChange(int channel, int data1, int data2, long tick)  {
        event(channel, ShortMessage.CONTROL_CHANGE, data1, data2, tick);
    }

    private void event(int channel, int type, int data1, int data2, long tick) {
        Except.run(()->
            sendMessage(
                new ShortMessage(type, channel, data1, data2),
                tick
            )
        );
    }

    private void sendMessage(ShortMessage msg, long tick) {
        currTrack.add(new MidiEvent(msg, tick));
    }

}
