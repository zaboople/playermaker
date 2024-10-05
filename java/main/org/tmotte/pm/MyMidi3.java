package org.tmotte.pm;
import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.Closeable;
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
 * Whereas Player is used to compose musical "tracks" (analagous but not exactly the same
 * as Midi Tracks), MyMidi is used for playback of the composition.
 */
public class MyMidi3 implements Closeable {

    /////////////////////////////////////////
    // STATIC CONSTANTS & DATA STRUCTURES: //
    /////////////////////////////////////////

    /** Unfortunately our drum channel is 9 and not 10 because Java is stupid. It works fine,
        just aggravating */
    public final static int DRUM_CHANNEL=9;

    /** Largely arbitrary but makes it possible for us to use all our standard divisions from
        whole note to 128th note: */
    public final static int SEQUENCE_RESOLUTION=Divisions.whole * 5 * 7;

    /** This isn't really used, but it's just to signify that the number
        of ticks in a second is twice the resolution:*/
    public final static int TICKS_PER_SECOND=SEQUENCE_RESOLUTION*2;

    private final static int TICKS_PER_MINUTE=TICKS_PER_SECOND*60;
    private final static int REVERB = 91;


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

    // Simpler state:
    long tickX=0;
    private Map<String, MetaInstrument> instrumentsByName;
    private boolean async=false;

    // Standard Midi objects:
    private Sequencer sequencer;
    private Sequence sequence;
    private Synthesizer synth;
    private MidiChannel[] midiChannels;
    private Instrument[] instruments;

    // Custom objects:
    private final MidiTracker midiTracker=new MidiTracker();
    private final ReserveChannels reserveChannels=new ReserveChannels();
    private final SwellGen swellGen=new SwellGen(()->tickX, midiTracker::sendExpression);
    private final BendGen bendGen=new BendGen(()->tickX, midiTracker::sendBend);
    private final SequencerWatcher sequencerWatcher;

    /** Shortcut to MyMidi3(Optional.empty()) */
    public MyMidi3() {
        this(Optional.empty());
    }

    /** Creates a new Midi sequencer and synthesizer, ready to play
        @param replaceInstruments Attempts to replace built-in Instruments
        with a file (usually a .sf2 file) containing alternates. Kind of
        inflexible and not really well tested.
    */
    public MyMidi3(Optional<File> replaceInstruments) {
        Except.run(()-> {
            synth=MidiSystem.getSynthesizer();
            synth.open();
            setInstruments(
                SequencerUtils.getOrReplaceInstruments(synth, replaceInstruments)
            );
            sequencer=MidiSystem.getSequencer();
            SequencerUtils.hookSequencerToSynth(sequencer, synth);
            sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
            setBeatsPerMinute(60);
            midiChannels = synth.getChannels();
        });
        sequencerWatcher=new SequencerWatcher(sequencer, synth);
    }

    /** Defaults to false, so that when playing when play
        methods are called and synth starts up, MyMidi3 stops and waits for
        playing to finish.
        @param async True or false
        @return this */
    public MyMidi3 setAsync(boolean async) {
        this.async=async;
        return this;
    }

    private MyMidi3 setInstruments(Instrument... instruments) {
        this.instruments=instruments;
        instrumentsByName=MetaInstrument.map(true, instruments);
        return this;
    }

    /** Gets the array of all instruments that have been loaded.
        @return array of instruments */
    public Instrument[] getInstruments() {
        return this.instruments;
    }

    /** Gets an instrument by looking for an exact match by name.
        @param name Name to match
        @return Instrument found, or throws Exception if none found */
    public Instrument getInstrument(String name) {
        MetaInstrument i = instrumentsByName.get(name);
        if (i==null)
            throw new RuntimeException("Not found: "+name);
        return i.instrument;
    }

    /** Finds Instruments that match names reasonably well
        @param names A String that approximately matches what you are looking
        for. It will be treated as a space-delimited series of keywords
        to match.
        @return list of instruments found - may be empty if none found.
    */
    public List<Instrument> findInstruments(String names) {
        return findMetaInstruments(names).map(mi->mi.instrument)
            .toList();
    }
    /** Attempts to find an Instrument that is a best match for name.
       @param name Same as for findInstruments(name).
       @throws IllegalArgumentException if none found, or more than one
       found.
       @return an Instrument
    */
    public Instrument findInstrument(String name) {
        final List<MetaInstrument> found = findMetaInstruments(name).toList();
        if (found.size()==0)
            throw new IllegalArgumentException("No instrument found for: \""+name+"\"");
        if (found.size() > 1)
            throw new IllegalArgumentException(
                "More than one instrument found for \""+name+ "\": "+
                    found.stream().map(it -> it.displayName)
                        .collect(Collectors.joining(", "))
            );
        return found.get(0).instrument;
    }
    private Stream<MetaInstrument> findMetaInstruments(String name) {
        final String[] names = name.toLowerCase().split(" ");
        return instrumentsByName.values().stream()
            .filter(i -> {
                for (String n: names)
                    if (!i.searchName.contains(n))
                        return false;
                return true;
            });
    }

    final void setBeatsPerMinute(int bpm) {
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

    /** Sequences the necessary tracks and runs it through the attached synth.
        @param players A series of Players containing data to be sequenced
        @return this
    */
    public MyMidi3 playAndStop(Player... players)  {
        return play(true, players);
    }
    /** Sequences the necessary tracks and runs it through the attached synth.
        Closes the sequencer afterwards if asked to do so.
        @param players A series of Players containing data to be sequenced
        @param andThenClose Whether to close the internal sequencer after
            playing.
        @return this
    */
    public MyMidi3 play(boolean andThenClose, Player... players) {
        sequence(players);
        return play(andThenClose);
    }
    private MyMidi3 play(boolean andThenClose) {
        sequencerWatcher.closeOnFinishPlay(andThenClose);
        try {
            //System.out.println("MyMidi3.play() starting..."+sequencer+" "+andThenClose);
            if (!sequencer.isOpen())
                sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(0);
            sequencer.setLoopStartPoint(0);
            sequencer.setTickPosition(0);
            sequencer.start();
            if (!async)
                sequencerWatcher.waitForFinish();
        } catch (Exception e) {
            close();
            throw new RuntimeException(e);
        }
        return this;
    }

    /** Stops internal sequencer. */
    public void stopPlay() {
        sequencer.stop();
    }

    /** Writes internal sequence to a midi file.
        @param file The file to use */
    public void write(File file) {
        final int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
        final int result = Except.get(()-> MidiSystem.write(sequence, fileTypes[0], file));
        if (result == -1) {
            throw new RuntimeException("Write didn't work");
        }
    }


    /** Closes internal sequencer and synth. */
    public @Override void close() {
        sequencer.close();
        synth.close();
    }

    /** Resets the internal sequence and does other cleanup in preparation
        for creating a new composition.
        @return this
    */
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

    /** Creates a sequence from contents of the given players.
        To actually play that sequence, call play() after this.
        @param players An array of Player objects containing data
            to be sequenced.
        @return this
    */
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
        channelAttrs.reverb=player.reverb();
        channelAttrs.mainChannel=player.channel();
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
                currTick=processChordEvent(chord, player, channelAttrs, currTick, noParent);
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
            Chord<?> chord, Player player, ChannelAttrs channelAttrs,
            long currTick, ParentState parentState
        ) {

        ////////////////
        // 1. SETUP:  //
        ////////////////

        final long chordTick   =currTick  + (tickX * chord.restBefore());
        final long chordEndTick=chordTick + (tickX * chord.duration());
        final boolean hasBends=!chord.bends().isEmpty(),
                    hasChords=!chord.chords().isEmpty(),
                    hasSwells=!chord.swells().isEmpty();

        // For a real rest, we do an early return, because we're not
        // playing any sound, just skipping ahead:
        if (chord.isTrueRest())
            return chordEndTick+1;

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
        public void reserve(Player player) {
            reservedAll[player.channel()]=true;
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
        // While reverb is not a recordable event, we are allowing it here
        // because it is a function of the channel, and each channel has its own setting, and
        // each channel can be used by different players at different times.
        midiChannels[channel].controlChange(REVERB, channelAttrs.reverb);
        midiTracker.sendBendSense(channel, channelAttrs.bendSense, currTick);
        midiTracker.sendPressure(channel, channelAttrs.pressure, currTick);
        midiTracker.sendInstrument(channel, channelAttrs.instrument, currTick);
    }

}
