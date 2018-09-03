package org.tmotte.pm;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import javax.sound.midi.Instrument;

/**
 * FIXME Player needs to set channel in the constructor.
 *
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
 *    <li>Beats per minute, commonly abbreviated as "BPM". This can be used to speed up & slow down
 *        play at various points during the composition.
 *    <li>Bend sensitivity: Refer to {@link BendContainer} for more information.
 *    <li>Instrument
 *    <li>Channel: While this can be set more than once, it generally isn't useful to change its initial
 *        setting (arguably channel should be a constructor parameter for Player()).
 * </ul>
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
public class Player extends AttributeHolder<Player> implements Notable {
    private static class TimeTracking {
        long timeUpToIndex=0;
        long timeAtIndex=0;
        int indexForTimeCounted=0;
    }
    private TimeTracking timeTracker=new TimeTracking();
    private List<Event> events=new ArrayList<>();
    private int reverb=0;
    private long startTime=0;
    private boolean reverbSetOnce=false;

    public Player() {
        super();
        volume(64);
    }

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
        Chord chord=events.get(index).getChord();
        return chord==null ?0L :chord.totalDuration();
    }


    /**
     * BPM means "beats per minute". This setting is event-based, so it affects only the notes added after BPM is changed.
     * <br>
     * However: BPM is really a function of the MyMidi sequencer; when it changes, all Players are affected. So if Player A sets BPM
     * to 60 and plays a quarter note, and Player B also plays a quarter note, both notes will play for one second. Thus it's easiest
     * think of Player A as a "lead" that the others will automatically follow when they slow down or speed up, which is fairly
     * similar to real-world situations.
     */
    public Player setBeatsPerMinute(int bpm) {
        return event(new Event().setBeatsPerMinute(bpm));
    }
    public Player setBPM(int bpm) {
        return setBeatsPerMinute(bpm);
    }
    public Player bpm(int bpm) {
        return setBeatsPerMinute(bpm);
    }

    public Collection<Event> events() {
        return events;
    }

    public Player setBendSensitivity(int sensitivity) {
        event(new Event().setBendSensitivity(sensitivity));
        return this;
    }
    public Player bendSense(int sensitivity) {
        return setBendSensitivity(sensitivity);
    }

    public Player setPressure(int pressure) {
        event(new Event().setPressure(pressure));
        return this;
    }

    private Player event(Event e) {
        events.add(e);
        return this;
    }

    /** Warning: Reverb can only be set once */
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

    /** This and the other numbered r#() methods are legacy & deprecated. */
    public Player r1() {return rest(Divisions.reg2);}
    public Player r2() {return rest(Divisions.reg2);}
    public Player r4() {return rest(Divisions.reg4);}
    public Player r8() {return rest(Divisions.reg8);}
    public Player r16() {return rest(Divisions.reg16);}
    public Player r32() {return rest(Divisions.reg32);}
    public Player r64() {return rest(Divisions.reg64);}

    public Player r8_3() {return rest(Divisions.triplet8);}
    public Player r16_3() {return rest(Divisions.triplet16);}
    public Player r32_3() {return rest(Divisions.triplet32);}
    public Player r64_3() {return rest(Divisions.triplet64);}

    private Player rest(long division) {
        int v=volume();
        volume(0);
        addChord(division, 0);
        volume(v);
        return this;
    }


    /** For internal use, required by Notable */
    public @Override Chord addChord(long duration, int... pitches) {
        Chord chord=new Chord(this, duration, pitches);
        events.add(new Event(chord));
        return chord;
    }

    /** For internal use, required by Notable */
    public @Override Note addNote(long duration, int pitch) {
        return addChord(duration, pitch).notes().get(0);
    }

    /** For internal use, required by AttributeHolder &amp; BendContainer*/
    protected @Override Player self(){
        return this;
    }
}
