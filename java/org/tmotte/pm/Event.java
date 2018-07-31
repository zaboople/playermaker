package org.tmotte.pm;
import javax.sound.midi.Instrument;

/**
 * Used by Player to record changes. This is roughly speaking, a pseudo-Track.
 * Most Events will be Chords.
 */
public class Event {

    private Chord chord=null;
    private Instrument instrument=null;
    private String instrumentName=null;

    // FIXME we can make more efficient storage here, either
    // by nesting an "AnythingButChord" class, or making one
    // int value and a short switch to tell us which type it is.
    private static class AnythingButChord {
    }
    private Integer instrumentIndex=null;
    private Integer channel=null;
    private Integer bendSensitivity=null;
    private Integer bpm=null;


    public Event() {
    }
    public Event(Chord chord) {
        this.chord=chord;
    }
    public Event(Instrument instrument) {
        this.instrument=instrument;
    }
    public Event setInstrument(int index){//FIXME remove
        this.instrumentIndex=index;
        return this;
    }
    public Event setInstrument(String name){
        this.instrumentName=name;
        return this;
    }
    public Event setChannel(Integer channel){
        this.channel=channel;
        return this;
    }
    public Event setBendSensitivity(Integer BendSensitivity){
        this.bendSensitivity=bendSensitivity;
        return this;
    }
    public Event setBeatsPerMinute(Integer bpm){
        this.bpm=bpm;
        return this;
    }


    public Chord getChord() {
        return chord;
    }
    public Instrument getInstrument() {
        return instrument;
    }
    public Integer getInstrumentIndex() {
        return instrumentIndex;
    }
    public String getInstrumentName() {
        return instrumentName;
    }
    public Integer getChannel() {
        return channel;
    }
    public Integer getBendSensitivity() {
        return bendSensitivity;
    }
    public Integer getBeatsPerMinute() {
        return bpm;
    }
}