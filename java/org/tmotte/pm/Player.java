package org.tmotte.pm;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import javax.sound.midi.Instrument;

public class Player extends AttributeHolder<Player> implements Notable {
    private static class TimeTracking {
        long timeUpToIndex=0;
        long timeAtIndex=0;
        int indexForTimeCounted=0;
    }
    private TimeTracking timeTracker=new TimeTracking();
    private List<Event> events=new ArrayList<>();
    private int reverb=0, pressure=0;
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
    /**
       Player can only store 1 track and 1 channel, so if this is invoked multiple times,
       the last values are the only ones that count.

       @param Channel: When there are multiple players, you are responsible for assigning each
       its own channel. If possible, create "gaps" between the tracks for different
       players; MyMidi will make use of these unused channels in cases where
       one player needs extra channels, most common example being bent & not-bent
       notes in the same Chord.
     */
    public Player instrumentChannel(int instrumentIndex, int channelIndex) {
        channel(channelIndex);
        instrument(instrumentIndex);
        return this;
    }
    public Player channel(int channel) {
        return event(new Event().setChannel(channel)); //FIXME should we default to zero somehow?
    }




    public Player setStart(long time) {
        this.startTime=time;
        return this;
    }
    public long getStart() {
        return startTime;
    }
    public long getEndTime() {
        return startTime + getTimeLength();
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
     * BPM means "beats per minute". Different players can play at their own speeds (might be... trickY) or one player can act as
     * "lead", setting the BPM for everyone - as long as they are the first player sequenced. This is event-based, so it can be set
     * more than once, affecting all Chords added afterwards.
     * <br>
     * MyMidi uses this setting until told otherwise, so the next Player will inherit it (usually desirable) unless they set it explicitly.
     */
    public Player setBeatsPerMinute(int bpm) {
        return event(new Event().setBeatsPerMinute(bpm));
    }
    public Player setBPM(int bpm) {
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

    public Player r(long i) {return rest(i);}
    public Player r(int i) {return rest(Divisions.convert(i));}
    public Player r(double d) {return rest(Divisions.convert(d));}

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
