package org.tmotte.common.midi;
import javax.sound.midi.Instrument;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * This deals with various quirks I've noticed about instrument names.
 */
public class MetaInstrument {
    public final static String categories[] = {
        "Piano", "Chromatic Perc.", "Organ", "Guitar",
        "Bass", "Strings", "Ensemble", "Brass",
        "Reed", "Pipe", "Synth Lead", "Synth Pad",
        "Synth Effects", "Ethnic", "Percussive", "Sound Effects"
    };

    public final String originalName;
    public final String displayName;
    public final String searchName;
    public final Instrument instrument;
    public final boolean categorized;

    public MetaInstrument(Instrument instrument, Optional<String> category) {
        this.instrument=instrument;
        originalName=instrument.getName().trim();
        categorized = category.isPresent();
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
        searchName=displayName.toLowerCase();
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
        iterate(
	        useDefaultCategories,
	        mi -> map.put(mi.displayName, mi),
	        instruments
		);
		return map;
    }

    public static void iterate(
            boolean useDefaultCategories,
            Consumer<MetaInstrument> consumer,
            Instrument... instruments
        ) {
		int catIndex=-1;
		for (int i=0; i<instruments.length; i++) {
			catIndex+=(i % 8 == 0) ?1 :0;
            MetaInstrument mi=new MetaInstrument(
	            instruments[i],
                Optional.ofNullable(
                    useDefaultCategories && catIndex<8 ?categories[catIndex] :null
                )
            );
	        consumer.accept(mi);
		}
    }


}