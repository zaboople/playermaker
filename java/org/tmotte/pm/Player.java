package org.tmotte.pm;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class Player extends AttributeHolder<Player> implements Notable {
    static class TimeTracking {
        long timeUpToIndex=0;
        long timeAtIndex=0;
        int indexForTimeCounted=0;
    }
    TimeTracking timeTracker=new TimeTracking();

    long startTime=0;

    List<Chord> sounds=new ArrayList<>();
    int bendSensitivity=2;
    int reverb=0;

    // This might be better on attrs:
    int instrumentIndex=0, channelIndex=0, trackIndex=0;

    public Player() {
        super(new TonalAttributes());
        volume(64);
    }

    public Player instrument(int instrumentIndex) {
        this.instrumentIndex=instrumentIndex;
        return this;
    }
    public Player instrumentChannel(int instrumentIndex, int channelIndex) {
        this.instrumentIndex=instrumentIndex;
        this.channelIndex=channelIndex;
        return this;
    }
    public Player instrumentTrackChannel(int instrumentIndex, int trackIndex, int channelIndex) {
        this.instrumentIndex=instrumentIndex;
        this.channelIndex=channelIndex;
        return this;
    }

    public long getEndTime() {
        return startTime + getTimeLength();
    }

    public long getTimeLength() {
        int chordCount=sounds.size();
        while (timeTracker.indexForTimeCounted < chordCount-1)
            timeTracker.timeUpToIndex +=
                sounds.get(timeTracker.indexForTimeCounted++).totalDuration();
        long last=chordCount==0 ?0 :sounds.get(timeTracker.indexForTimeCounted).totalDuration();
        return timeTracker.timeUpToIndex + last;
    }

    public Collection<Chord> sounds() {
        return sounds;
    }

    public Player setStart(long time) {
        this.startTime=time;
        return this;
    }
    public Player setBendSensitivity(int sensitivity) {
        this.bendSensitivity=sensitivity;
        return this;
    }
    public Player bendSense(int sensitivity) {
        this.bendSensitivity=sensitivity;
        return this;
    }
    public int getBendSensitivity() {
        return this.bendSensitivity;
    }

    public Player setReverb(int reverb) {
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
        Chord sound=new Chord(this, duration, pitches);
        sounds.add(sound);
        return sound;
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
