package org.tmotte.keyboard;
import javax.sound.midi.*;
import java.util.*;
import java.util.function.Consumer;
import org.tmotte.common.midi.MetaInstrument;

public class MetaInstruments {
    private final Map<String, Integer> displayNameToIndex=new HashMap<>();
    private final List<MetaInstrument> metaInstruments=new ArrayList<>();

    public void init(Instrument[] instruments, boolean useDefaultCategories) {
        displayNameToIndex.clear();
        metaInstruments.clear();
        List<MetaInstrument> more=useDefaultCategories
            ?new ArrayList<>(instruments.length)
            :null;
        List<MetaInstrument> drums=useDefaultCategories
            ?new ArrayList<>(instruments.length)
            :null;
        MetaInstrument.iterate(
	        useDefaultCategories,
	        mi -> {
	            if (mi.categorized || !useDefaultCategories)
	                metaInstruments.add(mi);
	            else
	            if (mi.displayName.startsWith("Drumkit:"))
	                drums.add(mi);
	            else
	                more.add(mi);
            },
            instruments
		);
        if (more!=null) {
            add(drums);
            add(more);
        }
        int len=metaInstruments.size();
        for (int i=0; i<len; i++)
	        displayNameToIndex.put(metaInstruments.get(i).displayName, i);
    }

    private void add(List<MetaInstrument> list) {
        Collections.sort(list, (mi1, mi2)->mi1.displayName.compareTo(mi2.displayName));
        for (MetaInstrument mi: list)
            metaInstruments.add(mi);
    }


    public void searchByName(String search, Consumer<String> callback) {
        search=Optional.ofNullable(search)
	        .map(s -> s.toLowerCase().trim())
            .filter(s -> !s.equals("") && !s.equals("*"))
            .orElse(null);
		for (MetaInstrument mi: metaInstruments)
			if (search==null || mi.searchName.contains(search))
                callback.accept(mi.displayName);
    }
    public MetaInstrument get(String displayName) {
        return get(displayNameToIndex.get(displayName));
    }
    public MetaInstrument get(int index) {
        return metaInstruments.get(index);
    }
}