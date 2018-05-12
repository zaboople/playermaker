package org.tmotte.pm;
import javax.sound.midi.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

/** Look in java System properties for sound values. */
public class MyMidiOld  {

    final static int PROGRAM = 192;
    final static int NOTEON = 144;
    final static int NOTEOFF = 128;
    final static int SUSTAIN = 64;
    final static int REVERB = 91;
    Optional<Recorder> recorder=Optional.empty();
    Sequencer sequencer;
    Synthesizer synthesizer;
    Instrument instruments[];
    Instrument instrument;
    int instrumentIndex;
    ChannelData channels[];
    ChannelData metaChannel;    // current channel
    final int[] notes;

    /*
        metaChannel.velocity = value;
        channel.setChannelPressure(metaChannel.pressure);
        channel.setPitchBend(metaChannel.bend);
        channel.controlChange(REVERB, metaChannel.reverb);
        channel.setMute(metaChannel.mute);
        channel.controlChange(SUSTAIN, metaChannel.sustain ? 127 : 0);

        channel.setSolo(metaChannel.solo);
        channel.setMono(metaChannel.mono);
    */

	public MyMidiOld() throws Exception {
        final int transpose = 24;
		notes=new int[12*8];
		for (int i=0; i<notes.length; i++)
			notes[i]=transpose+i;

	    // This is supposed to be closed when done:
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();

        // And this too:
        sequencer = MidiSystem.getSequencer();
        Soundbank sb = synthesizer.getDefaultSoundbank();
        instruments = sb.getInstruments();
        synthesizer.loadInstrument(instruments[0]);

        MidiChannel midiChannels[] = synthesizer.getChannels();
        channels = new ChannelData[midiChannels.length];
        for (int i = 0; i < channels.length; i++)
            channels[i] = new ChannelData(midiChannels[i], i);
        metaChannel=channels[0];
	}

    /**
     * Stores MidiChannel information.
     */
    class ChannelData {

        MidiChannel channel;
        boolean solo, mono, mute, sustain;
        int velocity, pressure, bend, reverb;
        int index;

        public ChannelData(MidiChannel channel, int index) {
            this.channel = channel;
            this.index = index;
            velocity = pressure = bend = reverb = 64;
        }
    } // End class ChannelData

	private void changeChannel(int channelIndex) {
        metaChannel = channels[channelIndex];
		MidiChannel channel=metaChannel.channel;
	}




    private void play(int noteNum) {
        metaChannel.channel.noteOn(noteNum, metaChannel.velocity);
        recorder.ifPresent(r->r.createShortEvent(NOTEON, noteNum));
    }

    private void stop(int noteNum) {
        metaChannel.channel.noteOff(noteNum, metaChannel.velocity);
        recorder.ifPresent(r->r.createShortEvent(NOTEOFF, noteNum));
    }


    private void programChange(int i) {
	    this.instrument=instruments[i];
        if (instruments != null)
            synthesizer.loadInstrument(instrument);
        metaChannel.channel.programChange(i);
        recorder.ifPresent(r-> {
            r.createShortEvent(PROGRAM, i);
        });
    }

    public void close() {
	    synthesizer.close();
	    sequencer.close();
    }




	private void killChannel(int i) {
        channels[i].channel.allNotesOff();
	}




    /**
     * A frame that allows for midi capture & saving the captured data.
     */
    class Recorder implements MetaEventListener {
	    class TrackData {
	        Integer chanNum; String name; Track track;
	        public TrackData(int chanNum, String name, Track track) {
	            this.chanNum = new Integer(chanNum);
	            this.name = name;
	            this.track = track;
	        }
	    }
	    private Sequencer sequencer;
	    private ChannelData metaChannel;
	    private Track track;
	    boolean isRecording;
	    long startTime;
	    Sequence sequence;

        List<TrackData> tracks = new ArrayList<>();

        public Recorder(Sequencer sequencer, ChannelData metaChannel) {
	        this.sequencer=sequencer;
	        this.metaChannel=metaChannel;
            sequencer.addMetaEventListener(this);
            try {
                sequence = new Sequence(Sequence.PPQ, 10);
            } catch (Exception ex) { ex.printStackTrace(); }
		}

        public void meta(MetaMessage message) {
            if (message.getType() == 47) {  // 47 is end of track
	            System.out.println("Hey");
            }
        }

		public void record() {
			isRecording=true;
            track = sequence.createTrack();
            startTime = System.currentTimeMillis();

            // Add a program change right at the beginning of
            // the track for the current instrument
            createShortEvent(PROGRAM, metaChannel.index);
        }
        public void stopRecording() {
            sequencer.removeMetaEventListener(this);
            isRecording=false;
        }
        public void play() throws Exception {
            if (!sequencer.isOpen())
                sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setLoopStartPoint(0);
            sequencer.setTickPosition(0);
            sequencer.start();
        }
        public void stopPlay() {
            sequencer.stop();
        }

	    /**
	     * given 120 bpm:
	     *   (120 bpm) / (60 seconds per minute) = 2 beats per second
	     *   2 / 1000 beats per millisecond
	     *   (2 * resolution) ticks per second
	     *   (2 * resolution)/1000 ticks per millisecond, or
	     *      (resolution / 500) ticks per millisecond
	     *   ticks = milliseconds * resolution / 500
	     */
	    public void createShortEvent(int type, int num) {
	        ShortMessage message = new ShortMessage();
	        try {
	            long millis = System.currentTimeMillis() - startTime;
	            long tick = millis * sequence.getResolution() / 500;
	            message.setMessage(type+metaChannel.index, num, metaChannel.velocity);
	            MidiEvent event = new MidiEvent(message, tick);
	            track.add(event);
	        } catch (Exception ex) { ex.printStackTrace(); }
	    }
		public void write(File file) throws Exception {
	        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
	        if (MidiSystem.write(sequence, fileTypes[0], file) == -1) {
		        throw new Exception("Write didn't work");
	        }
	    }
    }


    public static void main(String args[]) throws Exception {
	    MyMidiOld mm=new MyMidiOld();
	    for (int i=0; i<20; i++){
		    mm.play(44);
		    mm.play(48);
		    mm.play(51);
		    Thread.sleep(100);
		    mm.stop(44);
		    mm.stop(48);
		    mm.stop(51);
		    Thread.sleep(200);
		    mm.play(44);
		    mm.play(48);
		    mm.play(51);
		    Thread.sleep(100);
		    mm.stop(44);
		    mm.stop(48);
		    mm.stop(51);

		    mm.play(49);
		    mm.play(53);
		    mm.play(56);
		    Thread.sleep(200);

		    mm.stop(49);
		    mm.stop(53);
		    mm.stop(56);
		    Thread.sleep(100);
		    mm.play(49);
		    mm.play(53);
		    mm.play(56);
		    Thread.sleep(200);
		    mm.stop(49);
		    mm.stop(53);
		    mm.stop(56);
		    System.out.println(i);
	    }
	    Thread.sleep(200);
    }
}
