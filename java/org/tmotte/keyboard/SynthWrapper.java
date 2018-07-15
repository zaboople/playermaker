package org.tmotte.keyboard;
import javax.sound.midi.*;
import java.util.*;
import java.util.function.Supplier;
import java.io.File;
import java.io.IOException;

import org.tmotte.common.function.Exceptional;

public class SynthWrapper implements MetaEventListener {

    private final static int PROGRAM = 192; //1100.0000
    private final static int NOTEON = 144;
    private final static int NOTEOFF = 128;

    private MetaInstruments metaInstruments=new MetaInstruments();
    private java.util.List<MetaTrack> tracks = new ArrayList<>();
    private Sequencer sequencer;
    private Sequence sequence;
    private Synthesizer synthesizer;
    private MetaChannel channels[];
    private MetaChannel cc;    // current channel
    private Track track;
    private long startTime;
    private boolean recording;
    private Runnable sequenceCallback;

	private static class MetaTrack  {
	    Integer chanNum; String name; Track track;
	    MetaTrack(int chanNum, String name, Track track) {
	        this.chanNum = chanNum;
	        this.name = name;
	        this.track = track;
	    }
	}

    public SynthWrapper init(Optional<File> instrumentFile, Optional<File> sequenceFile) throws Exception {

	    // Set up synthesizer & sequencer:
        if (synthesizer == null && (synthesizer = MidiSystem.getSynthesizer()) == null)
            throw new RuntimeException("getSynthesizer() failed!");
        synthesizer.open();
        sequencer = MidiSystem.getSequencer();
        sequencer.addMetaEventListener(this);

		// The sequencer is not necessarily using the same synthesizer as you'd expect. But
		// more importantly, there is a BIG difference between these two:
		//   -  Sequencer.getTransmitters(): Gets existing transmitters, if there are any
		//   -  Sequencer.getTransmitter(): CREATES a new transmitter, even if one or more
		//      already exist.
		// The API works this way so that a sequencer can transmit the same thing to multiple resources;
		// the problem is that getTransmitter() should be renamed createTransmitter().
		for (Transmitter t: sequencer.getTransmitters())
			Optional.ofNullable(t.getReceiver()).ifPresent(Receiver::close);
		sequencer.getTransmitters().stream()
	        .findFirst()
	        .orElse(sequencer.getTransmitter())
	        .setReceiver(synthesizer.getReceiver());


		// Create default sequence:
        sequenceFile.ifPresentOrElse(
	        file -> Exceptional.run( ()->openSequence(file) )
	        ,
            ()-> Exceptional.run( ()->sequence=new Sequence(Sequence.PPQ, 10) )
        );


		// Meta-instruments:
		final Instrument[] instruments=instrumentFile
			.map(file ->
				Exceptional.get(()->{
					synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
					Soundbank soundbank=MidiSystem.getSoundbank(file);
					//synthesizer.loadAllInstruments(soundbank);
					return soundbank.getInstruments();
				})
			).orElseGet(()->
				synthesizer.getDefaultSoundbank().getInstruments()
			);
		metaInstruments.init(instruments, true);
        synthesizer.loadInstrument(metaInstruments.get(0).instrument);

		// Meta channels:
	    MidiChannel midiChannels[] = synthesizer.getChannels();
	    channels = new MetaChannel[midiChannels.length];
	    for (int i = 0; i < channels.length; i++)
	        channels[i] = new MetaChannel(midiChannels[i], i);
	    cc = channels[0];
	    return this;
    }


	public void setSequenceEndCallback(Runnable r) {
		this.sequenceCallback=r;
	}

    public void close() {
        if (synthesizer != null)
            synthesizer.close();
        if (sequencer != null)
            sequencer.close();
        sequencer = null;
        synthesizer = null;
        metaInstruments=null;
        channels = null;
    }
	public MetaInstruments getMetaInstruments() {
		return metaInstruments;
	}
	public void setChannel(int index) {
	    cc = channels[index];
	}
	public MetaChannel getChannel() {
		return cc;
	}
	public int getChannelCount() {
		return channels.length;
	}



	public void startRecord(MetaInstrument instr) {
		recording = true;
        track = sequence.createTrack();
        startTime = System.currentTimeMillis();
        createInstrumentEvent(instr);
	}

	public void stopRecord(Optional<MetaInstrument> meta) {
        //MetaTrack tr=null;
    	//for (MetaTrack maybe: tracks)
        //	if (maybe.chanNum==cc.channelIndex+1)
        //    	tr=maybe;
    	//if (tr==null)
            tracks.add(new MetaTrack(cc.channelIndex+1, meta.map(m -> m.displayName).orElse("?"), track));
        //else {
	    //    tr.track=track;
	    //    tr.name=name;
        //}
        recording=false;
	}
	public void playBack() {
        Exceptional.run(()-> {
            if (!sequencer.isOpen())
                sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setLoopStartPoint(0);
            sequencer.setTickPosition(0);
	        sequencer.start();
        });
	}
	public boolean recording() {
		return recording;
	}
	public void stopPlayback() {
        sequencer.stop();
	}


	public void setTrackChannel(int index, int c) {
        tracks.get(index).chanNum = c;
	}
	public void setTrackName(int index, String s) {
        tracks.get(index).name = s;
	}
	public int getTrackChannel(int index) {
        return tracks.get(index).chanNum;
    }
    public String getTrackName(int index) {
        return tracks.get(index).name;
	}
	public int getTrackCount() {
		return tracks.size();
	}
	public boolean deleteTrack(int index) {
		MetaTrack track=tracks.remove(index);
		sequence.deleteTrack(track.track);
		return true;
	}


    public void instrumentChannelChange(MetaInstrument mi, int channelIndex) {
	    setChannel(channelIndex);
        synthesizer.loadInstrument(mi.instrument);
        cc.channel.programChange(mi.getBank(), mi.getProgram());
        if (recording)
            createInstrumentEvent(mi);
    }

    public int suggestChannel(MetaInstrument mi, int currChannel) {
	    boolean[] channelsUsed=new boolean[channels.length];
	    int nominate=-1;
	    for (MetaTrack track: tracks)
		    if (track.chanNum>-1) {
			    channelsUsed[track.chanNum-1]=true;
			    if (mi.displayName.equals(track.name))
				    nominate=track.chanNum-1;
		    }
	    if (mi.displayName.startsWith("Drumkit:"))
		    return 10-1;
	    if (currChannel!=10-1 && (
			    recording || !channelsUsed[currChannel]
		    ))
		    return currChannel;
	    if (nominate!=-1)
		    return nominate;
	    for (int i=0; i<channelsUsed.length; i++)
		    if (!channelsUsed[i])
			    return i;
	    return currChannel;
    }

    public void saveMidiFile(File file) {
	    Exceptional.run(()->{
            int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
            if (fileTypes.length == 0)
                throw new RuntimeException("MidiSystem doesn't support any file types!");
            else
            if (MidiSystem.write(sequence, fileTypes[0], file) == -1)
                throw new RuntimeException("Problems writing to file...?");
        });
    }
	public void openSequence(File f) throws Exception {
		this.sequence=MidiSystem.getSequence(f);
		Track[] newTracks=sequence.getTracks();
		tracks.clear();
		int i=0;
		for (Track track: newTracks)
			tracks.add(new MetaTrack(-1, ""+(i++), track));
	}

	public void allNotesOff() {
        for (int i = 0; i < channels.length; i++) {
            channels[i].channel.allNotesOff();
        }
	}
	public void sendNoteOn(int note) {
        cc.sendNoteOn(note);
        if (recording)
            createShortEvent(NOTEON, note);
	}
	public void sendNoteOff(int note) {
        cc.sendNoteOff(note);
        if (recording)
            createShortEvent(NOTEOFF, note);
    }

	private void createInstrumentEvent(MetaInstrument mi) {
		Exceptional.run(()-> {
			int bank = mi.getBank(),
				program=mi.getProgram();
			int msgType = ShortMessage.CONTROL_CHANGE;
			int chan= cc.channelIndex;
	        sendMessage(
		        new ShortMessage(msgType, chan, 0,  bank >> 7)   // = 9
	        );
	        sendMessage(
		        new ShortMessage(msgType, chan, 32, bank & 0x7f) // = 0
	        );
	        createShortEvent(PROGRAM, program);
        });
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
    private void createShortEvent(int type, int num) {
        Exceptional.run(()->{
	        ShortMessage message = new ShortMessage();
            message.setMessage(type+cc.channelIndex, num, cc.getVolume());
            sendMessage(message);
        });
    }

    private void sendMessage(ShortMessage msg) {
        long millis = System.currentTimeMillis() - startTime;
        long tick = millis * sequence.getResolution() / 500;
        track.add(new MidiEvent(msg, tick));
    }

    public @Override void meta(MetaMessage message) {
        if (message.getType() == 47) {  // 47 is end of track
            sequenceCallback.run();
        }
    }

}
