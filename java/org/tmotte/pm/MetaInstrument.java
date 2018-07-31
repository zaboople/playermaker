package org.tmotte.pm;
import javax.sound.midi.Instrument;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/* FIXME promote back to keyboard? */
public class MetaInstrument {
    public final static String categories[] = {
        "Piano", "Chromatic Perc.", "Organ", "Guitar",
        "Bass", "Strings", "Ensemble", "Brass",
        "Reed", "Pipe", "Synth Lead", "Synth Pad",
        "Synth Effects", "Ethnic", "Percussive", "Sound Effects"
    };

    public final String originalName;
    public final String displayName;
    public final Instrument instrument;

    public MetaInstrument(Instrument instrument, Optional<String> category) {
        this.instrument=instrument;
        originalName=instrument.getName().trim();
        displayName = category
            .map(s->s+" - "+originalName)
            .orElse(
                Optional.ofNullable(
                        instrument.toString().startsWith("Drumkit:")
                            ?"Drumkit: "
                            :null
                    )
                    .map(s -> s + originalName)
                    .orElse(originalName)
            );
    }
    public int getProgram() {
        return instrument.getPatch().getProgram();
    }
    public int getBank() {
        return instrument.getPatch().getBank();
    }


    public static Map<String, MetaInstrument> map(Instrument... instruments) {
        return map(false, instruments);
    }
    public static Map<String, MetaInstrument> map(
            boolean useDefaultCategories,
            Instrument... instruments
        ) {
        return map(useDefaultCategories, new HashMap<>(), instruments);
    }
    public static Map<String, MetaInstrument> map(
            boolean useDefaultCategories,
            Map<String, MetaInstrument> map,
            Instrument... instruments
        ) {
		int catIndex=-1;
		for (int i=0; i<instruments.length; i++) {
			int ci=catIndex+=i % 8==0 ?1 :0;
            MetaInstrument mi=new MetaInstrument(
	            instruments[i],
                Optional.ofNullable(
                    useDefaultCategories && ci<8 ?categories[ci] :null
                )
            );
	        map.put(mi.displayName, mi);
		}
		return map;
    }

}