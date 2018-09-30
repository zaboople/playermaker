package test;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import org.tmotte.common.midi.SequencerUtils;
import org.tmotte.pm2.MyMidi3;
import test.hear2.Test7Slash16Time;
import static org.tmotte.pm.Pitches.*;

/**
 */
public class TestSoundBank  {
    public static void main(String args[]) throws Exception {
	    if (args.length<1)
		    System.err.println("Need a file & optionally, an instrument name.");
		Optional<File> file=Optional.ofNullable(
				args.length>0 ?args[0] :null
			)
			.map(name -> new File(name));
		MyMidi3 midi=new MyMidi3(file);
		Instrument[] instruments=midi.getInstruments();
		List<Instrument> insList=Arrays.asList(instruments)
			.stream().sorted(
				(x, y)->x.getName().compareTo(y.getName())
			)
			.collect(Collectors.toList());
		for (Instrument inst: insList)
			System.out.println(inst.getName()+" "+inst.getPatch().getBank()+" "+inst.getPatch().getProgram());
		System.out.println("-----------------------------");
		System.out.println("Total count: "+insList.size());

		Map<String, Instrument> insMap = new HashMap<>();
		insList.stream().forEach(instr -> insMap.put(instr.getName(), instr));

		Optional<String> instrName=Optional.ofNullable(
				args.length>1 ?args[1] :null
			);
		System.out.println("-------------");
		System.out.println(
			instrName.map(name -> {
					new Test7Slash16Time().test(midi, true, midi.getInstrument(name));
					return "";
				}).orElse("Next time give me an instrument name and I'll play it.")
		);
    }
}
