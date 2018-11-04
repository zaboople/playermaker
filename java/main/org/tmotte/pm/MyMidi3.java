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
import org.tmotte.common.text.Log;


/**
 * FIXME name MyMidiSequencer
 * Whereas Player is used to compose musical "tracks" (analagous but not exactly the same
 * as Midi Tracks), MyMidi is used for playback of the composition.
 */
public class MyMidi3  {

    /////////////////////////////////////////
    // STATIC CONSTANTS & DATA STRUCTURES: //
    /////////////////////////////////////////

    // The latter is inversely related to the other:
    public final static int SEQUENCE_RESOLUTION=Divisions.whole * 5 * 7;

    // This isn't really used, but it's just to signify that the number
    // of ticks in a second is twice the resolution, no matter what
    // resolution you give:
    public final static int TICKS_PER_SECOND=SEQUENCE_RESOLUTION*2;
    public final static int TICKS_PER_MINUTE=TICKS_PER_SECOND*60;

    private final static int REVERB = 91;
    private final static int DRUM_CHANNEL=9;

    private static class ChannelAttrs {
        int mainChannel;
        int bendSense=2;
        int reverb=0;
        int pressure=0;
        Instrument instrument;
    }

    private static class ParentState {
        final boolean exists, bending;
        int channel;
        public ParentState(boolean exists, boolean bending, int channel) {
            this.exists=exists;
            this.bending=bending;
            this.channel=channel;
        }
    }

    ////////////////////////////////////////////
    // INSTANCE VARIABLES AND INITIALIZATION: //
    ////////////////////////////////////////////

    long tickX=0;

    private Sequencer sequencer;
    private Sequence sequence;
    private Synthesizer synth;
    private MidiChannel[] midiChannels;
    private Instrument[] instruments;

    private boolean waitForSequencerToStopPlaying=true;
    private final MidiTracker midiTracker=new MidiTracker();
    private Map<String, MetaInstrument> instrumentsByName;
    private SequencerWatcher sequencerWatcher;
    private ReserveChannels reserveChannels=new ReserveChannels();
    private SwellGen swellGen=new SwellGen(()->tickX, midiTracker::sendExpression);
    private BendGen bendGen=new BendGen(()->tickX, midiTracker::sendBend);

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

    void setBeatsPerMinute(int bpm) {
        tickX = Math.round(
            ((double)TICKS_PER_MINUTE) /
            (
                ((double)bpm) * ((double) Divisions.reg4)
            )
        );
        Log.log("MyMidi3", "TickX: {} ", tickX);
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

    public MyMidi3 reset() {
        Except.run(()-> {
            sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
        });
        reserveChannels.clear();
        return this;
    }

    ///////////////////////
    // SEQUENCE & WRITE: //
    ///////////////////////

    public MyMidi3 sequence(Player... players) {
        for (Player player: players)
            reserveChannels.reserve(player);
        for (Player player: players)
            sequencePlayer(player);
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
        ParentState noParent=new ParentState(false, false, -1);
        boolean firstChord=true;
        for (Event event: player.events()){
            final Chord<?> chord=event.getChord();
            if (chord==null)
                processNonChordEvent(event, channelAttrs, firstChord, currTick);
            else {
                if (firstChord) {
                    firstChord=false;
                    setupChannelForPlayer(channelAttrs.mainChannel, channelAttrs, currTick);
                }
                currTick=processChordEvent(chord, player, channelAttrs, currTick, noParent); //FIXME shouldn't it be +1?
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
            channelAttrs.mainChannel=eChannel;
            if (!firstChord)
                setupChannelForPlayer(channelAttrs.mainChannel, channelAttrs, currTick);
        });
        getAndSet(event.getBendSensitivity(), eBendSense -> {
            channelAttrs.bendSense=eBendSense;
            if (!firstChord)
                midiTracker.sendBendSense(channelAttrs.mainChannel, channelAttrs.bendSense,currTick);
        });
        getAndSet(event.getPressure(), ePressure -> {
            channelAttrs.pressure=ePressure;
            if (!firstChord)
                midiTracker.sendPressure(channelAttrs.mainChannel, channelAttrs.pressure, currTick);
        });
        getAndSet(getInstrument(event), eInst -> {
            channelAttrs.instrument=eInst;
            synth.loadInstrument(eInst);
            if (!firstChord)
                midiTracker.sendInstrument(channelAttrs.mainChannel, channelAttrs.instrument, currTick);
        });
    }
    private <T> void getAndSet(T t, java.util.function.Consumer<T> cons) {
        if (t!=null) cons.accept(t);
    }

    /** Only called by processNonChordEvent() */
    private Instrument getInstrument(Event event) {
        Instrument instrument=event.getInstrument();
        if (instrument!=null)
            return instrument;
        String instrumentName=event.getInstrumentName();
        if (instrumentName!=null) {
            MetaInstrument mi=instrumentsByName.get(instrumentName);
            if (mi==null)
                throw new IllegalStateException("No instrument named: \""+instrumentName+"\"");
            return mi.instrument;
        }
        return null;
    }

    private long processChordEvent(
            Chord<?> chord, Player player, ChannelAttrs channelAttrs, long currTick, ParentState parentState
        ) {

        ////////////////
        // 1. SETUP:  //
        ////////////////

        final long chordTick   =currTick  + (tickX * chord.restBefore());
        final long chordEndTick=chordTick + (tickX * chord.duration());
        final boolean hasBends=!chord.bends().isEmpty(),
                    hasChords=!chord.chords().isEmpty(),
                    hasSwells=!chord.swells().isEmpty();
        /*
            We only use a spare channel IF this chord has bends, AND:
                A. For a top-level chord, we only need a spare if there are sub-chords
                - OR -
                B. For a lower-level chord, we always need a spare, whether the main
                bent or not.
           You might notice that if everybody bends, then the main channel never gets used.
           The only example I can think of is a slide guitar situation, in which case we
           ought/might just use chord.bendWithParent(), in which case we specifically
           use the parent's channel (presumably even though we have no bends of our own).
        */
        final int channelIndex;
        if (chord.isBendWithParent()) {
            if (!parentState.exists)
                throw new RuntimeException("No parent to bend with");
            channelIndex=parentState.channel;
        }
        else
        if (hasBends && (parentState.exists || hasChords))
            channelIndex=reserveChannels.useSpare(player, channelAttrs, chordTick);
        else
            channelIndex=channelAttrs.mainChannel;
        if (chord.tag()!=null)
            Log.log("MyMidi3", "Chord: {}", chord.tag());
        Log.log(
            "MyMidi3",
            "Chord setup: Channel: {} restBefore: {} duration: {} pitches: {} start tick: {} end tick: {}",
            channelIndex, chord.restBefore(), chord.duration(), chord.pitches(), chordTick, chordEndTick
        );

        //////////////////
        // 2. EXECUTE:  //
        //////////////////

        // Send each note-on/off
        for (int pitch: chord.pitches()) {
            pitch+=chord.getTranspose();
            int volume=chord.volume();
            midiTracker.noteOn(channelIndex, pitch, (hasSwells ?127 :chord.volume()), chordTick);
            midiTracker.noteOff(channelIndex, pitch, chordEndTick);
        }

        // And swells:  n addition to SwellGen, when not doing swells, we send 0 if we detect
        // a top-level rest, and 127 (max) for regular notes.
        if (hasSwells)
            swellGen.handle(channelIndex, chordTick, chord.volume(), chord.swells());
        else
        if (chord.volume()==0 && !parentState.exists)
            midiTracker.sendExpression(channelIndex, 0, chordTick);
        else
            midiTracker.sendExpression(channelIndex, 127, chordTick);

        // And bends:
        if (hasBends) {
            bendGen.handle(channelIndex, chordTick, chord.bends());
            midiTracker.sendBendEnd(channelIndex, chordEndTick);
        }

        // And finally, sub-chords
        long realEndTick=chordEndTick;
        if (hasChords) {
            Log.log("MyMidi3", "Chord nesting... ");
            ParentState ps=new ParentState(true, hasBends, channelIndex);
            realEndTick=
                chord.chords().stream().map(sub ->
                        processChordEvent(
                            sub, player, channelAttrs, chordTick, ps
                        )
                    )
                    .reduce(realEndTick, (x,y) -> x > y ?x :y);
        }
        if (!parentState.exists)
            reserveChannels.clearSpares();
        Log.log("MyMidi3", "Chord complete, end tick: "+realEndTick);
        return realEndTick+1;
    }


    /** One instance exists within the main class; sort of ugly. */
    private class ReserveChannels {
        private boolean[] reservedAll=new boolean[16];
        private Set<Integer> currSpares=new HashSet<>();

        public void clear() {
            clearSpares();
            for (int i=0; i<reservedAll.length; i++)
                reservedAll[i]=false;
        }
        public void clearSpares() {
            currSpares.clear();
        }
        /** Players can overlap on a channel, assuming it won't cause a problem. */
        public void reserve(Player player) { //FIXME see this would be less stupid if we only allow channel to change once.
            for (Event event: player.events()) {
                Integer ch=event.getChannel();
                if (ch!=null)
                    reservedAll[ch]=true;
            }
        }
        public int useSpare(Player player, ChannelAttrs channelAttrs, long tick) {
            for (int ch=channelAttrs.mainChannel+1; ch<reservedAll.length; ch++) {
                if (ch==DRUM_CHANNEL)
                    ch++;
                if (reservedAll[ch])
                    break; //Because we don't go beyond the gap
                if (!currSpares.contains(ch)) {
                    //Log.log("MyMidi3", "Selected spare: "+ch);
                    currSpares.add(ch);
                    setupChannelForPlayer(ch, channelAttrs, tick);
                    return ch;
                }
            }
            throw new RuntimeException(
                "Ran out of spare channels after "+channelAttrs.mainChannel+"; "
               +"Make sure to give enough space between the channels of different players, "
               +"or put players on the same channel when you know they can't interfere with "
               +"each other. Spare channels are needed when overlapping chords are bent."
            );
        }
    }

    private void setupChannelForPlayer(int channel, ChannelAttrs channelAttrs, long currTick)  {
        midiChannels[channel].controlChange(REVERB, channelAttrs.reverb); //FIXME this is not a midi event and should be treated differently.
        midiTracker.sendBendSense(channel, channelAttrs.bendSense, currTick);
        midiTracker.sendPressure(channel, channelAttrs.pressure, currTick);
        midiTracker.sendInstrument(channel, channelAttrs.instrument, currTick);
    }

    public void write(File file) throws Exception {
        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
        if (MidiSystem.write(sequence, fileTypes[0], file) == -1) {
            throw new Exception("Write didn't work");
        }
    }


}
