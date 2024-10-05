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
 * A general-purpose wrapper for sending Midi Messages to a given Track. Essentially this provides a
 * lot of sequencing methods that the Java Midi API should already have, but "forgot" to include.
 * Tracks can communicate with multiple channels, so technically you could use one track for everything
 * assuming you don't have two instruments playing the same note one the same track &amp; channel, which would
 * cause conflict (refer to noteOn &amp; noteOff).
 * A lot of these were quite difficult to figure out, and some - like portamento - were never tested.
 */
public class MidiTracker  {

    private Track currTrack;

    /** Creates a track-less MidiTracker, so that we'll blow up if you forget to set one*/
    public MidiTracker() {}

    /** Creates a new MidiTracker using t as its target.
        @param t The target track for messages.
    */
    public MidiTracker(Track t) {
        this.currTrack=t;
    }

    /** Creates a new Track using <code>sequence</code> and uses that as our target.
        @param sequence The sequencer to create a track with, via sequence.createTrack().
        No - you can't create Tracks directly via <code>new Track()</code>.
        @return this
    */
    public MidiTracker createTrack(Sequence sequence) {
        return setTrack(sequence.createTrack());
    }

    /** No real need to use this - prefer createTrack() instead.
        @param t The track to target messages to.
        @return this
    */
    public MidiTracker setTrack(Track t) {
        currTrack=t;
        return this;
    }

    /**
     * Send a note-on message to the channel. Plays until a corresponding
     * note-off message is sent.
     * @param channel The midi channel
     * @param pitch Semitone note value, 0-based
     * @param volume Volume in 0-127 range
     * @param tick Point in time
     */
    public void noteOn(int channel, int pitch, int volume, long tick) {
        // NOTE_ON=144
        Log.log("MidiTracker", "noteOn() tick {} pitch {} volume {}", tick, pitch, volume);
        event(channel, ShortMessage.NOTE_ON, pitch, volume, tick);
    }

    /**
     * Send a note-off message to the channel. Notice that multiple notes may be
     * playing at the same time, but the one selected is the one with designated
     * pitch (ignoring bend messages).
     * @param channel The midi channel
     * @param pitch Semitone note value, 0-based
     * @param tick Point in time
     */
    public void noteOff(int channel, int pitch, long tick) {
        event(channel, ShortMessage.NOTE_OFF, pitch, 0, tick);
    }

    /**
     * Send an instrument change to the channel.
     * @param channel The midi channel
     * @param instr Midi instrument to use
     * @param tick Point in time to switch instruments.
     */
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
    /**
        Send a bend
        A bend is 14 bits - no, not 16. That won't fit in a byte, but
        even better, you are required to split it into a 7-bits-each pair.
        We do that for you.
        @param channel Channel
        @param amount A denominator to go with your bend sensitivity numerator,
            divide to get the number (possibly fractional) of semitones to bend
            up or down by.
        @param tick Time to send message
    */
    public void sendBend(int channel, int amount, long tick) {
        Log.log("MidiTracker", "Bend {} at {} ", amount, tick);
        event(channel, ShortMessage.PITCH_BEND, getLSB(amount), getMSB(amount), tick);
    }
    /**
        Sends a message to stop the bend started by sendBend
        @param channel Channel
        @param tick Time to send message
    */
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

    /**
     * Try to turn off portamento
     *
     * @param channel Channel
     * @param tick Time to send message
     */
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
        sendMessage(
            Except.get(()->new ShortMessage(type, channel, data1, data2)),
            tick
        );
    }

    private void sendMessage(ShortMessage msg, long tick) {
        currTrack.add(new MidiEvent(msg, tick));
    }

}
