package org.tmotte.pm;
import javax.sound.midi.Instrument;
import java.util.function.Function;

/**
 * Used by Player to record changes. This is roughly speaking, a pseudo-Track.
 * Most Events will be Chords.
 */
public class Event {

    // FIXME we can make more efficient storage here, either
    // by nesting an "AnythingButChord" class, or making one
    // int value and a short switch to tell us which type it is.
    private static class AnythingButChord {
        private Instrument instrument=null;
        private String instrumentName=null;
        private Integer instrumentIndex=null;
        private Integer channel=null;
        private Integer bendSense=null;
        private Integer bpm=null;
        private Integer pressure=null;
    }

    private Chord chord=null;
    private AnythingButChord anything=null;

    public Event() {
    }
    public Event(Chord chord) {
        this.chord=chord;
    }
    public Event(Instrument instrument) {
        anything().instrument=instrument;
    }
    public Event setInstrument(int index){//FIXME remove
        anything().instrumentIndex=index;
        return this;
    }
    public Event setInstrument(String name){
        anything().instrumentName=name;
        return this;
    }
    public Event setChannel(Integer channel){
        anything().channel=channel;
        return this;
    }
    public Event setBendSensitivity(Integer bendSense){
        anything().bendSense=bendSense;
        return this;
    }
    public Event setPressure(Integer pressure){
        anything().pressure=pressure;
        return this;
    }
    public Event setBeatsPerMinute(Integer bpm){
        anything().bpm=bpm;
        return this;
    }


    public boolean hasChord() {
        return chord!=null;
    }
    public Chord getChord() {
        return chord;
    }
    public Instrument getInstrument() {
        return anything(a -> a.instrument);
    }
    public Integer getInstrumentIndex() {
        return anything(a -> a.instrumentIndex);
    }
    public String getInstrumentName() {
        return anything(a -> a.instrumentName);
    }
    public Integer getChannel() {
        return anything(a -> a.channel);
    }
    public Integer getBendSensitivity() {
        return anything(a -> a.bendSense);
    }
    public Integer getPressure() {
        return anything(a -> a.pressure);
    }
    public Integer getBeatsPerMinute() {
        return anything(a -> a.bpm);
    }

    private AnythingButChord anything() {
        return anything!=null ?anything :(anything=new AnythingButChord());
    }
    private <T> T anything(Function<AnythingButChord, T> f) {
        return anything==null ?null :f.apply(anything);
    }

}