package org.tmotte.pm;
import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.tmotte.common.function.Except;
import org.tmotte.common.midi.MetaInstrument;
import org.tmotte.common.midi.MidiTracker;
import org.tmotte.common.midi.SequencerUtils;
import org.tmotte.common.midi.SequencerWatcher;


/**
 * FIXME name MyMidiSequencer
 * Whereas Player is used to compose musical "tracks" (analagous but not exactly the same
 * as Midi Tracks), MyMidi is used for playback of the composition.
 */
public class MyMidi3  {

    // The latter is inversely related to the other:
    public final static int SEQUENCE_RESOLUTION=Divisions.whole * 5 * 7;

    // This isn't really used, but it's just to signify that the number
    // of ticks in a second is twice the resolution, no matter what
    // resolution you give:
    public final static int TICKS_PER_SECOND=SEQUENCE_RESOLUTION*2;
    public final static int TICKS_PER_MINUTE=TICKS_PER_SECOND*60;

    private final static int NO_BEND = 8192;
    private final static int REVERB = 91;
    private final static int DRUM_CHANNEL=9;

    private static class ChannelAttrs {
	    int bendSense=2;
	    int reverb=0;
	    int pressure=0;
	    Instrument instrument;
    }


    private Sequencer sequencer;
    private Synthesizer synth;
    private MidiChannel[] midiChannels;
    private Instrument[] instruments;
    private Map<String, MetaInstrument> instrumentsByName;
    private SequencerWatcher sequencerWatcher;

    private boolean waitForSequencerToStopPlaying=true;

    private MidiTracker midiTracker=new MidiTracker();
    private Sequence sequence;
    private ReserveChannels reservedChannels=new ReserveChannels();
    private int channelIndex=0;
    public long tickX=0;

    public MyMidi3() {
	    this(Optional.empty());
    }
    public MyMidi3(Optional<File> replaceInstruments) {
        Except.run(()-> {
            synth=MidiSystem.getSynthesizer();
            synth.open();
            setInstruments(
	            SequencerUtils.getOrReplaceInstruments(synth, replaceInstruments)
            );
            sequencer=MidiSystem.getSequencer();
            sequencerWatcher=new SequencerWatcher(sequencer);
            SequencerUtils.hookSequencerToSynth(sequencer, synth);
            sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
            setBeatsPerMinute(60);
			midiChannels = synth.getChannels();
        });
    }

    public MyMidi3 setInstruments(Instrument... instruments) {
        this.instruments=instruments;
        instrumentsByName=MetaInstrument.map(true, instruments);
        return this;
    }

    public Instrument[] getInstruments() {
	    return this.instruments;
    }

    public Instrument getInstrument(String name) {
	    return instrumentsByName.get(name).instrument;
    }


    public MyMidi3 reset() {
        Except.run(()-> {
            sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
        });
        return this;
    }

    public void write(File file) throws Exception {
        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
        if (MidiSystem.write(sequence, fileTypes[0], file) == -1) {
            throw new Exception("Write didn't work");
        }
    }


    public MyMidi3 sequence(Player... players) {
        for (Player player: players)
            reservedChannels.reserve(player);
        for (Player player: players)
            sequencePlayer(player);
        return this;
    }

	/** Exposed only for testing. */
    protected MyMidi3 setBeatsPerMinute(int bpm) {
        tickX = Math.round(
            ((double)TICKS_PER_MINUTE) /
            (
                ((double)bpm) * ((double) Divisions.reg4)
            )
        );
        return this;
    }

    private void sequencePlayer(Player player)  {
        // Track & time & defaults:
        midiTracker.setTrack(sequence.createTrack());
        long currTick=player.getStart() * tickX;
        ChannelAttrs channelAttrs=new ChannelAttrs();
        channelAttrs.reverb=player.getReverb();
        channelAttrs.instrument=instruments[0];

        // And blast off the rocket:
        boolean firstChord=true;
        for (Event event: player.events()){
	        final Chord chord=event.getChord();
	        if (chord==null)
		        processNonChordEvent(event, channelAttrs, firstChord, currTick);
			else {
	            if (firstChord) {
		            firstChord=false;
		            setupChannelForPlayer(channelIndex, channelAttrs, currTick);
	            }
	            currTick=processChordEvent(chord, player, channelAttrs, currTick);
            }
        }
    }

	/**
	 * This process events that affect the overall Channel, like bend sensitivity.
	 * Only sends messages to the channel if we've already started playing chords,
	 * as we do channel setup right before the first chord.
	 */
    private void processNonChordEvent(
		    Event event, ChannelAttrs channelAttrs, boolean firstChord, long currTick
	    ) {
        getAndSet(event.getBeatsPerMinute(), eBpm ->
	        setBeatsPerMinute(eBpm)
        );
        getAndSet(event.getChannel(), eChannel ->   {
            channelIndex=eChannel;
            if (!firstChord)
	            setupChannelForPlayer(channelIndex, channelAttrs, currTick);
        });
        getAndSet(event.getBendSensitivity(), eBendSense -> {
            channelAttrs.bendSense=eBendSense;
            if (!firstChord)
                midiTracker.sendBendSense(channelIndex, channelAttrs.bendSense,currTick);
        });
        getAndSet(event.getPressure(), ePressure -> {
            channelAttrs.pressure=ePressure;
            if (!firstChord)
                midiTracker.sendPressure(channelIndex, channelAttrs.pressure, currTick);
        });
        getAndSet(getInstrument(event), eInst -> {
            channelAttrs.instrument=eInst;
            synth.loadInstrument(eInst);
            if (!firstChord)
                midiTracker.sendInstrument(channelIndex, channelAttrs.instrument, currTick);
        });
    }
    private <T> void getAndSet(T t, java.util.function.Consumer<T> cons) {
	    if (t!=null) cons.accept(t);
    }

	/** Only called by processNonChordEvent() */
    private Instrument getInstrument(Event event) {
        Instrument instrument=event.getInstrument();
        String instrumentName=event.getInstrumentName();
        if (instrumentName!=null) {
            MetaInstrument mi=instrumentsByName.get(instrumentName);
            if (mi==null) throw new IllegalStateException("No instrument named: \""+instrumentName+"\"");
            instrument=mi.instrument;
        }
        Integer instrumentIndex=event.getInstrumentIndex();
        if (instrumentIndex!=null)
            instrument=instruments[instrumentIndex];
        return instrument;
    }

    private long processChordEvent(
		    Chord chord, Player player, ChannelAttrs channelAttrs, long currTick
	    ) {
        long soundStart=currTick;
        int wasChannel=channelIndex;
        for (Note note: chord.notes()) {

            // Local variables for note attributes:
            int pitch=note.pitch+note.getTranspose(),
                volume=note.getVolume();
            long restBefore=note.restBefore * tickX;
            List<Bend> noteBends=note.bends();

            // Note start time:
            currTick=soundStart + restBefore;

            // Send any bends, and the note start:
            if (!noteBends.isEmpty()) {
                channelIndex=reservedChannels.useSpare(channelIndex, player, channelAttrs, currTick);
                System.out.println("Using spare: "+channelIndex);
                sendBends(channelIndex, soundStart + restBefore, noteBends);
            }
            if (pitch<0)
                throw new RuntimeException("Invalid pitch "+pitch+" from player "+player);
            midiTracker.noteOn(channelIndex, pitch, volume, currTick);


            // Finish the note:
            currTick += note.duration * tickX;
            midiTracker.noteOff(channelIndex, pitch, currTick);
            if (!noteBends.isEmpty())
                midiTracker.eventBendEnd(channelIndex, currTick);

            // Get back on channel if we had to use a reserve:
            channelIndex=wasChannel;
        }

        // Finish up with the chord bends (if any) and
        // and advance the currTick counter:
        if (!chord.bends().isEmpty())
            sendBends(channelIndex, soundStart, chord.bends());
        currTick=soundStart+(chord.totalDuration() * tickX);
        if (!chord.bends().isEmpty())
            midiTracker.eventBendEnd(channelIndex, currTick);
	    return currTick;
    }


    /** One instance exists within the main class; ReserveChannels has hooks going back out to channelIndex; sort of ugly. */
    private class ReserveChannels {
        boolean[] reservedAll=new boolean[16];
        Map<Player, Set<Integer>> playerSetupAlready=new HashMap<>();

        /** Players can overlap on a channel, assuming it won't cause a problem. */
        public void reserve(Player player) {
	        for (Event event: player.events()) {
		        Integer ch=event.getChannel();
		        if (ch!=null)
		            reservedAll[ch]=true;
            }
        }
        public int useSpare(int channel, Player player, ChannelAttrs channelAttrs, long tick) {
	        if (channel+1==DRUM_CHANNEL)
		        channel++;
            if (!reservedAll[channel+1]) {
                channel+=1;
                Set<Integer> already=playerSetupAlready.computeIfAbsent(player, p -> new HashSet<>());
                if (!already.contains(channel)) {
                    System.out.println("Setting up spare...");
                    already.add(channel);
                    setupChannelForPlayer(channel, channelAttrs, tick);
                }
                return channel;
            }
            throw new RuntimeException(
                "Ran out of spare channels after "+channel+"; "
               +"Make sure to give enough space between the channels of different players, "
               +"or put players on the same channel when you know they can't interfere with "
               +"each other. Spare channels are needed when individual Notes are bent - as "
               +"opposed to bending entire Chords"
            );
        }
    }

	private void setupChannelForPlayer(int channel, ChannelAttrs channelAttrs, long currTick)  {
        midiChannels[channel].controlChange(REVERB, channelAttrs.reverb);
        midiTracker.sendBendSense(channel, channelAttrs.bendSense, currTick);
        midiTracker.sendPressure(channel, channelAttrs.pressure, currTick);
        midiTracker.sendInstrument(channel, channelAttrs.instrument, currTick);
    }

    private void sendBends(int channel, long soundStart, List<Bend> bends) {
        //System.out.println("MyMidi3: Bends "+bends.size());
        long t=soundStart;
        int pitch=NO_BEND;
        for (Bend bend: bends) {
            //System.out.println("Bend delay "+delay+" duration "+duration+" denominator "+denominator);
            t+=(bend.delay * tickX);
            int change = NO_BEND / bend.denominator;
            int perTicky = change / (int)bend.duration;
            int leftover = change % (int)bend.duration;
            int leftoverIncr=leftover>0 ?1 :-1;
            for (int i=0; i<bend.duration; i++) {
                int thisAmount = perTicky;
                if (leftover!=0) {
                    thisAmount+=leftoverIncr;
                    leftover-=leftoverIncr;
                }
                pitch+=thisAmount;
                if (pitch==16384) pitch=16383;
                //System.out.println("Sending bend "+pitch+" at "+t);
                midiTracker.eventBend(channel, pitch, t);
                t+=tickX;
            }
        }
    }


    ///////////
    // PLAY: //
    ///////////


    public MyMidi3 play() {
        return play(false);
    }
    public MyMidi3 playAndStop() {
        return play(true);
    }
    public MyMidi3 play(boolean stop, Player... players) {
        sequence(players);
        return play(stop);
    }
    public void playAndStop(Player... players)  {
        sequence(players);
        play(true);
    }
    public MyMidi3 play(boolean andThenStop) {
        sequencerWatcher.closeOnFinishPlay(andThenStop);
        try {
            //System.out.println("MyMidi3.play() starting..."+sequencer+" "+andThenStop);
            if (!sequencer.isOpen())
                sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(0);
            sequencer.setLoopStartPoint(0);
            sequencer.setTickPosition(0);
            sequencer.start();
            sequencerWatcher.waitForIf();
        } catch (Exception e) {
            sequencer.close();
            throw new RuntimeException(e);
        }
        return this;
    }

    public void stopPlay() {
        sequencer.stop();
    }
    public void close() {
        System.out.println("MyMidi3.close()");
        sequencer.close();
        //sequencer=null;
    }

}