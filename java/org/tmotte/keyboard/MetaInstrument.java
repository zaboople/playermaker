package org.tmotte.keyboard;
import javax.sound.midi.*;
public class MetaInstrument {
    public final String displayName;
    public final Instrument instrument;
    public final String originalName;
    public final String searchName;
    public final int index;
    public MetaInstrument(Instrument instrument, int index, String displayName, String originalName) {
        this.instrument=instrument;
        this.index=index;
        this.displayName=displayName;
        this.originalName=originalName;
        this.searchName=displayName.toLowerCase();
    }
    public int getProgram() {
        return instrument.getPatch().getProgram();
    }
    public int getBank() {
        return instrument.getPatch().getBank();
    }
}