package org.tmotte.keyboard;
import javax.sound.midi.*;
/**
 * Stores MidiChannel information.
 */
public  class MetaChannel {
    final int SUSTAIN = 64;
    final int REVERB = 91;
    private boolean solo, mono, mute, sustain;
    private int velocity, pressure, bend, reverb;

    MidiChannel channel;
    int channelIndex;

    public MetaChannel(MidiChannel channel, int index) {
        this.channel = channel;
        this.channelIndex = index;
        velocity = pressure = bend = reverb = 64;
    }
    public void sendNoteOn(int note) {
        channel.noteOn(note, velocity);
    }
    public void sendNoteOff(int note) {
        channel.noteOff(note, velocity);
    }

    public void setVolume(int value) {
        velocity=value;
    }
    public int getVolume() {return velocity;}
    public void setPressure(int value) {
        channel.setChannelPressure(pressure=value);
    }
    public int getPressure() {return pressure;}
    public void setBend(int value) {
        channel.setPitchBend(bend = value);
    }
    public int getBend() {return bend;}
    public void setReverb(int value) {
        channel.controlChange(REVERB, reverb = value);
    }
    public int getReverb(){return reverb;}
    public void setMute(boolean b) {
      channel.setMute(mute = b);
    }
    public boolean getMute(){return mute;}
    public void setSolo(boolean b) {
        channel.setSolo(solo = b);
    }
    public boolean getSolo(){return solo;}
    public void setMono(boolean b) {
        channel.setMono(mono = b);
    }
    public boolean getMono(){return mono;}
    public void setSustain(boolean b) {
        sustain=b;
        channel.controlChange(SUSTAIN, (sustain=b) ? 127 : 0);
    }
    public boolean getSustain(){return sustain;}

}
