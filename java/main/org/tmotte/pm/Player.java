package org.tmotte.pm;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import javax.sound.midi.Instrument;

/**
 * A Player is roughly analagous to a human musician, and thereby to a Midi Channel.
 * It can play any instrument, but only one instrument at any given time. A composition
 * can be made of many Players. Notes are added to a Player using methods inherited from
 * {@link Notable}.
 * <br>
 * <b>Event-based attributes</b>
 * While it may be unexpected, many of the attributes of Player cannot be attributes
 * of a Chord or Note because they are applied to the entire channel. Most of them can be changed
 * throughout the course of a composition, however, because these settings are treated as events,
 * the same as Chords/Notes. Thus a call to, say, setInstrument() will only affect the instrument for
 * notes added after that call is made.
 * <br>
 * These attributes include:
 * <ul>
 *    <li>Pressure: Which usually means vibrato. See {@link BendContainer} for more fine-grained vibrato
 *        as well as bend control.
 *    <li>Beats per minute: Commonly abbreviated as "BPM". This can be used to speed up & slow down
 *        play at various points during the composition.
 *    <li>Bend sensitivity: Refer to {@link BendContainer} for more information.
 *    <li>Instrument
 *    <li>Channel: While this can be set more than once, it generally isn't useful to change its initial
 *        setting (arguably channel should be a constructor parameter for Player()).
 * </ul>
 * <br>
 * And then we have: Reverb. For whatever reasons, the Java Sequencer ignores reverb events, so we apply
 * reverb directly to the synthesizer at the very beginning of playback, once and only once. This means
 * you can have only one reverb setting per Player; also, if you save your composition to a
 * standard Midi sequence file, any reverb settings are lost.
 * <br>
 * <b>Timing</b>
 * Timing values in Midi are called "ticks". PlayerMaker has its own separate internal system of ticks,
 * leaving the actual Midi ticks inaccessible. Normally you will use setBPM() in combination with
 * classical timing notation to control timing, but it is often useful to synchronize different players
 * using the internal relative timing with methods like {@link #setStartTime(long)} and {@link #getEndTime()}.
 */
public class Player extends NoteAttributeHolder<Player> implements Notable<Player> {
    private static class TimeTracking {
        long timeUpToIndex=0;
        long timeAtIndex=0;
        int indexForTimeCounted=0;
    }
    private TimeTracking timeTracker=new TimeTracking();
    private List<Event> events=new ArrayList<>();
    private int reverb=0;
    private boolean reverbSetOnce=false;
    private long startTime=0;
    private NoteAttributes attributes=new NoteAttributes();

    public Player() {
        super();
        volume(64);
    }

    //////////////////////////////////
    // INSTRUMENT / CHANNEL EVENTS: //
    //////////////////////////////////

    /** Achieves the equivalent of instrumentChannel(instrumentIndex, 0) */
    public Player instrument(int instrumentIndex) {
        return event(new Event().setInstrument(instrumentIndex));
    }
    public Player instrument(Instrument instrument) {
        return event(new Event(instrument));
    }
    public Player instrument(String name) {
        return event(new Event().setInstrument(name));
    }
    public Player instrumentChannel(Instrument instrument, int channel) {
        channel(channel);
        instrument(instrument);
        return this;
    }
    public Player instrumentChannel(int instrumentIndex, int channelIndex) {
        channel(channelIndex);
        instrument(instrumentIndex);
        return this;
    }
    /**
       Assigns the Player a Midi channel index, which is 0 by default. Two Players can
       use the same Midi channel, but since many sound effects and instrument settings
       are applied to the entire channel, sharing is likely to have undesirable effects.
       Most Midi sequencers allow 16 channels, with channel 10 reserved for percussion/drum
       instruments only.
       @param Channel: The channel index, usually constrained to 0-15 allowed values.
     */
    public Player channel(int channel) {
        return event(new Event().setChannel(channel));
    }

    ///////////////////////////////////
    // OTHER EVENT-BASED ATTRIBUTES: //
    ///////////////////////////////////

    /**
     * FIXME test BPM changes in mid-flight!
     * BPM means "beats per minute". This setting is event-based, so it affects only the notes added after BPM is changed.
     * <br>
     * However: BPM is really a function of the MyMidi pseudo-sequencer; when it changes, all Players are affected. So if Player A
     * sets BPM to 60 and plays a quarter note, and Player B also plays a quarter note, both notes will play for one second.
     * Thus it's easiest think of Player A as a "lead" that the others will automatically follow when they slow down or speed up,
     * which is similar to real-world situations. Thus it's also important to pass Player A as the first Player argument to
     * MyMidi.play(playerA, playerB...).
     */
    public Player bpm(int bpm) {
        return setBeatsPerMinute(bpm);
    }
    public Player setBeatsPerMinute(int bpm) {
        return event(new Event().setBeatsPerMinute(bpm));
    }
    public Player setBPM(int bpm) {
        return setBeatsPerMinute(bpm);
    }

    /**
     * In Midi a bend can go a "whole step" by default, which is to say, two notes up or down.
     * However, you can change the "Bend sensitivity" to increase the range from a whole step to many more
     * steps. Refer to FIXME for actual bend methods.
     */
    public Player setBendSensitivity(int sensitivity) {
        event(new Event().setBendSensitivity(sensitivity));
        return this;
    }
    /** A shortcut to setBendSensitivity(int) */
    public Player bendSense(int sensitivity) {
        return setBendSensitivity(sensitivity);
    }

    public Player setPressure(int pressure) {
        event(new Event().setPressure(pressure));
        return this;
    }
    public Player pressure(int pressure) {
        return setPressure(pressure);
    }

    /** Note: Reverb can only be set once, because it is not event-based like most other attributes. */
    public Player reverb(int reverb) {
        return setReverb(reverb);
    }

    /** Note: Reverb can only be set once, because it is not event-based like most other attributes. */
    public Player setReverb(int reverb) {
        if (reverbSetOnce)
            throw new RuntimeException("There is no point in setting the reverb more than once.");
        reverbSetOnce=true;
        this.reverb=reverb;
        return this;
    }
    public int getReverb() {
        return reverb;
    }


    /////////////////////////////
    // START / END / DURATION: //
    /////////////////////////////

    /**
     * Sets the start time in ticks. Changing the start time <i>after</i> adding notes/chords/rests is forbidden
     * and will throw an IllegalStateException
     */
    public Player setStartTime(long time) {
        for (Event e: events)
            if (e.hasChord())
                throw new IllegalStateException("Cannot change start time after events are added.");
        this.startTime=time;
        return this;
    }
    public Player setStart(long time) {
        this.startTime=time;
        return this;
    }
    /** Gets the start time in ticks. */
    public long getStartTime() {
        return startTime;
    }
    public long getStart() {
        return startTime;
    }
    public long getEndTime() {
        return getEnd();
    }
    public long getEnd() {
        return startTime + getTimeLength();
    }
    /** Gets the duration of the composition in ticks. */
    public long duration() {
        return getTimeLength();
    }
    public long getTimeLength() {
        int eventCount=events.size();
        while (timeTracker.indexForTimeCounted < eventCount-1)
            timeTracker.timeUpToIndex += getDuration(timeTracker.indexForTimeCounted++);
        long last=eventCount>0
            ?getDuration(timeTracker.indexForTimeCounted)
            :0;
        return timeTracker.timeUpToIndex + last;
    }
    private long getDuration(int index) {
        Chord<?> chord=events.get(index).getChord();
        return chord==null ?0L :chord.totalDuration();
    }


    ///////////////////////////////////
    // RESTS (NOT LIKE CHORD RESTS): //
    ///////////////////////////////////


    /**
     * The "r" is short for "rest". The Player will pause for
     * the specified duration. Use rest(int) for whole/half/quarter/eigth/etc. notes,
     * and {@link #rest(double)} for dotted & triplet rests.
     * <br>
     * Internally, a rest is actually represented as a Chord.
     */
    public Player r(int duration) {return rest(Divisions.convert(duration));}
    public Player r(double duration) {return rest(Divisions.convert(duration));}
    /**
     * This special version of rest() allows one to specify a rest duration using the internal timing
     * values by getTimeLength(), getStart(), getEndTime(), etc.
     */
    public Player r(long i) {return rest(i);}

    private Player rest(long division) {
        int v=volume();
        volume(0);
        addChord(division, 0);
        volume(v);
        return this;
    }

    /////////////
    // CHORDS: //
    /////////////

    /**
     * Adds a Chord made of the given notes for the specified duration, and returns that Chord, which can be further modified.
     * Duration and notes work the same as for @link{#p(int, int...)}
     */
    public Chord<Player> c(int duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    public Chord<Player> c(double duration, int... notes) {
        return addChord(Divisions.convert(duration), notes);
    }
    private Chord<Player> addChord(long duration, int... pitches) {
        var chord=new Chord<>(this, attributes, duration, pitches);
        events.add(new Event(chord));
        return chord;
    }


    /**
     * Adds a Chord made of the given notes for the specified duration, and returns the original Player object.
     * @param duration Use 1 for a whole note, 2 for a half note, 4 for a quarter, and so on. For dotted notes
     *       and triplets, use {@link #p(double, int...)}
     * @param notes Follows the 12-tone western scale, with low C at 0, D&#x266d; at 1, and so on, allowing up
     *       as many octaves high as the synthesizer can perform. Note values directly correspond to the midi
     *       standard.
     */
    public Player p(int duration, int... notes) {
        return c(duration, notes).up();
    }

    /**
     * An alternate version of {@link #p(int, int...)} that accepts a double, allowing
     * dotted and triplet notes, e.g. "8." and "8.3" as respective examples.
     */
    public Player p(double duration, int... notes) {
        return c(duration, notes).up();
    }



    //////////////////////
    // EVENT INTERNALS: //
    //////////////////////

    protected Collection<Event> events() {
        return events;
    }
    private Player event(Event e) {
        events.add(e);
        return this;
    }

    //////////////////////////////////////////
    // NoteAttributesHolder IMPLEMENTATION: //
    //////////////////////////////////////////


    protected @Override Player setVolume(int v) {
        getNoteAttributesForWrite().volume=v;
        return this;
    }
    protected @Override Player setTranspose(int semitones) {
        getNoteAttributesForWrite().transpose=semitones;
        return this;
    }
    protected @Override NoteAttributes getNoteAttributesForRead(){
        return attributes;
    }
    private NoteAttributes getNoteAttributesForWrite(){
        return attributes=new NoteAttributes(attributes);
    }

}
