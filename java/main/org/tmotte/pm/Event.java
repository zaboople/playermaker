package org.tmotte.pm;
import javax.sound.midi.Instrument;
import java.util.function.Function;

/**
 * Used by Player to record changes. This is roughly speaking, a pseudo-MidiMessage.
 * Most Events will be Chords. MyMidi reads the Events back out during playback.
 */
class Event {

    /**
     * This is sort of a shortcut around a more typical (and more verbose) design
     * where Event is subclassed into 8 or 9 specializations for each type of event:
     * Bleagh. Instead, since most Events are Chords, Event has only two instance variables:
     <ul>
       <li>Chord
       <li>AnythingButChord
     </ul>
     * Then we just jam the union of all possible non-chord events into AnythingButChord.
     * Each event type is exclusive of all others (there's no such thing as a union type, so
     * take it as implied), and it's kind of wasteful to have a bunch of null pointers,
     * but since the "anything" instance variable is itself usually null, the waste isn't so bad.
     */
    private static class AnythingButChord {
        private Instrument instrument=null;
        private String instrumentName=null;
        private Integer instrumentIndex=null;
        private Integer channel=null;
        private Integer bendSense=null;
        private Integer bpm=null;
        private Integer pressure=null;
    }

    private Chord<?> chord=null;
    private AnythingButChord anything=null;

    public Event() {
    }
    public Event(Chord<?> chord) {
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
    public Chord<?> getChord() {
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

    /** For setting things. */
    private AnythingButChord anything() {
        return anything!=null ?anything :(anything=new AnythingButChord());
    }
    /** For getting things. */
    private <T> T anything(Function<AnythingButChord, T> f) {
        return anything==null ?null :f.apply(anything);
    }

}