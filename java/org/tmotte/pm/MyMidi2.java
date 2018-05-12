package org.tmotte.pm;
import javax.sound.midi.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

/** Look in java System properties for sound values. */
public class MyMidi2  {

    class MetaChannel {
        MidiChannel channel;
        boolean solo, mono, mute, sustain;
        int velocity, pressure, bend, reverb;
        int index;

        public MetaChannel(MidiChannel channel, int index) {
            this.channel = channel;
            this.index = index;
            velocity = pressure = bend = reverb = 64;
        }
    }

	// The latter is inversely related to the other:
	final static int SEQUENCE_RESOLUTION=Divisions.whole * 4;
	final static int BPM_FACTOR=(60 * 30);

	// This isn't really used, but it's just to signify that the number
	// of ticks in a second is twice the resolution, no matter what
	// resolution you give:
	final static int TICKS_PER_SECOND=SEQUENCE_RESOLUTION*2;

    final static int PROGRAM = 192;
    final static int NOTEON = 144;
    final static int NOTEOFF = 128;
    final static int SUSTAIN = 64;
    final static int REVERB = 91;
    final static int SEQUENCER_END_PLAY=47;

    final static int C=0, D=2, E=4, F=5, G=7, A=9, B=11;

    int transpose = 24;
    Track track;
    Sequencer sequencer;
    Sequence sequence;
    Synthesizer synthesizer;
    Instrument instruments[];
    Instrument instrument;
    int instrumentIndex;
    MetaChannel channels[];
    MetaChannel currChan;    // current channel
    long currTick=0;
    long beatTicks=TICKS_PER_SECOND;

    /*
        currChan.velocity = value;
        channel.setChannelPressure(currChan.pressure);
        channel.setPitchBend(currChan.bend);
        channel.controlChange(REVERB, currChan.reverb);
        channel.setMute(currChan.mute);
        channel.controlChange(SUSTAIN, currChan.sustain ? 127 : 0);

        channel.setSolo(currChan.solo);
        channel.setMono(currChan.mono);
    */

	public MyMidi2() {
		try {
	        synthesizer = MidiSystem.getSynthesizer();
	        synthesizer.open();
	        Soundbank sb = synthesizer.getDefaultSoundbank();
	        instruments = sb.getInstruments();

	        MidiChannel midiChannels[] = synthesizer.getChannels();
	        channels = new MetaChannel[midiChannels.length];
	        for (int i = 0; i < channels.length; i++)
	            channels[i] = new MetaChannel(midiChannels[i], i);
	        currChan=channels[0];

	        sequencer = MidiSystem.getSequencer();
			sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
	        setBeatsPerMinute(90);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		init();
	}

	public void init() {
	}


    public void play(boolean andThenStop) throws Exception {
        if (!sequencer.isOpen())
            sequencer.open();
        sequencer.setSequence(sequence);
	    sequencer.setLoopCount(0);
        sequencer.setLoopStartPoint(0);
        sequencer.setTickPosition(0);
        if (andThenStop)
            sequencer.addMetaEventListener(
	            event ->{
		            if (event.getType() == SEQUENCER_END_PLAY){
			            sequencer.close();
			            sequencer=null;
		            }
	            }
            );
        sequencer.start();
    }

    public void stopPlay() {
        sequencer.stop();
    }

	public void write(File file) throws Exception {
        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
        if (MidiSystem.write(sequence, fileTypes[0], file) == -1) {
	        throw new Exception("Write didn't work");
        }
    }

    public void close() {
	    synthesizer.close();
    }

	public void channel(int channelIndex) {
        currChan = channels[channelIndex];
	}

	public void track() {
	}

    public MyMidi2 setBeatsPerMinute(int beats) {
	    double d=((double) BPM_FACTOR) / ((double)beats);
	    beatTicks=Math.round(d);
	    return this;
    }

    public void instrument(int i) {
		track=sequence.createTrack();
	    this.instrument=instruments[i];
        //synthesizer.loadInstrument(instrument);
        //currChan.channel.programChange(i);
        event(PROGRAM, i, 0);
    }


    public MyMidi2 x4(int... notes) {
		return notes(Divisions.reg4, notes);
    }
    public MyMidi2 _4(int note) {
	    return note(Divisions.reg4, note);
    }
    public MyMidi2 _8(int note) {
	    return note(Divisions.reg8, note);
    }
    public MyMidi2 _16(int note) {
	    return note(Divisions.reg16, note);
    }


    public MyMidi2 note(long division, int note) {
	    division*=beatTicks;
	    note+=transpose;
        event(NOTEON, note, 0);
        event(NOTEOFF, note, division);
        return this;
    }
    public MyMidi2 notes(long division, int... notes) {
	    division*=beatTicks;
	    for (int note: notes)
	        event(NOTEON, note+transpose, 0);
        currTick+=division;
	    for (int note: notes)
	        event(NOTEOFF, note+transpose, 0);
        return this;
    }

    public MyMidi2 octave(int octave) {
	    transpose=octave*12;
	    return this;
    }
    public MyMidi2 move(int octaves) {
	    transpose+=octaves*12;
	    return this;
    }
    public MyMidi2 modulate(int notes) {
	    transpose+=notes;
	    return this;
    }



    private void event(int type, int num, long ticks) {
        ShortMessage message = new ShortMessage();
        try {
	        message.setMessage(type+currChan.index, num, currChan.velocity);
        } catch (Exception e) {
	        throw new RuntimeException(e);
        }
        currTick += ticks;// * sequence.getResolution();
        MidiEvent event = new MidiEvent(message, currTick);
        track.add(event);
    }


    public static void main(String args[]) throws Exception {
    }

}
