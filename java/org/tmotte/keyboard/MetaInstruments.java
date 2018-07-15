package org.tmotte.keyboard;
import javax.sound.midi.*;
import java.util.*;
import java.util.function.Consumer;

public class MetaInstruments {
    private final static String categories[] = {
        "Piano", "Chromatic Perc.", "Organ", "Guitar",
        "Bass", "Strings", "Ensemble", "Brass",
        "Reed", "Pipe", "Synth Lead", "Synth Pad",
        "Synth Effects", "Ethnic", "Percussive", "Sound Effects"
    };
    private final Map<String, Integer> displayNameToIndex=new HashMap<>();
    private MetaInstrument[] metaInstruments;

    public void init(Instrument[] instruments, boolean useDefaultCategories) {
        displayNameToIndex.clear();
        metaInstruments=new MetaInstrument[instruments.length];
		int catIndex=-1;
        List<MetaInstrument> more=useDefaultCategories
            ?new ArrayList<>(instruments.length)
            :null;
        List<MetaInstrument> drums=useDefaultCategories
            ?new ArrayList<>(instruments.length)
            :null;
        int lastCategorized=-1;
		for (int i=0; i<instruments.length; i++) {
			int ci=catIndex+=i % 8==0 ?1 :0;
            boolean categorized=useDefaultCategories && ci<8;
            String originalName=instruments[i].getName().trim();
            String displayName =
                Optional.ofNullable(
                    categorized ?categories[ci] :null
                )
                .map(s->s+" - "+originalName)
                .orElse(
                    Optional.ofNullable(
                            instruments[i].toString().startsWith("Drumkit:")
                                ?"Drumkit: "
                                :null
                        )
                        .map(s -> s + originalName)
                        .orElse(originalName)
				);
            MetaInstrument mi=new MetaInstrument(instruments[i], i, displayName, originalName);
            if (categorized)
                add(mi, lastCategorized=i);
            else
            if (displayName.startsWith("Drumkit:"))
                drums.add(mi);
            else
                more.add(mi);
		}
        if (more!=null) {
            lastCategorized=add(drums, lastCategorized);
            lastCategorized=add(more, lastCategorized);
        }
    }

    private int add(List<MetaInstrument> list, int currIndex) {
        Collections.sort(list, (mi1, mi2)->mi1.displayName.compareTo(mi2.displayName));
        for (int i=0; i<list.size(); i++)
            add(list.get(i), ++currIndex);
        return currIndex;
    }
    private void add(MetaInstrument mi, int index) {
        metaInstruments[index]=mi;
        displayNameToIndex.put(mi.displayName, index);
    }


    public void searchByName(String search, Consumer<String> callback) {
        search=search!=null
            ?search.toLowerCase().trim()
            :null;
        search=search!=null && (search.equals("") || search.equals("*"))
            ?null
            :search;
		for (MetaInstrument mi: metaInstruments)
			if (search==null || mi.searchName.contains(search))
                callback.accept(mi.displayName);
    }
    public MetaInstrument get(String displayName) {
        return get(displayNameToIndex.get(displayName));
    }
    public MetaInstrument get(int index) {
        return metaInstruments[index];
    }
}