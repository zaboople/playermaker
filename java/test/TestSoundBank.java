package test;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import org.tmotte.pm.Divisions;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
import static org.tmotte.pm.Pitches.*;

/**
 */
public class TestSoundBank  {
    public static void main(String args[]) throws Exception {
	    if (args.length<1)
		    throw new Exception("Need a file & optionally, an instrument name.");
		File file=new File(args[0]);
		if (!file.exists())
			throw new Exception("Can't load nonexistent file "+file);

		Soundbank soundbank=MidiSystem.getSoundbank(file);
		Instrument[] instruments=soundbank.getInstruments();
		List<Instrument> insList=Arrays.asList(instruments)
			.stream().sorted(
				(x, y)->x.getName().compareTo(y.getName())
			)
			.collect(Collectors.toList());
		Map<String, Instrument> insMap=insList
			.stream().collect(
				Collectors.toMap(Instrument::getName, i->i)
			);
		for (Instrument inst: insList)
			System.out.println(inst.getName()+" "+inst.getPatch().getBank()+" "+inst.getPatch().getProgram());
		System.out.println("-----------------------------");
		System.out.println("Total count: "+insList.size());


		if (args.length>1) {
			String instrName=args[1];
			Instrument instr=insMap.get(instrName);
			if (instr==null) throw new Exception("Not found: "+instrName);
			System.out.println("-------------");
			System.out.println("Selected: "+instr.getName()+": "+instr.getPatch().getBank()+": "+instr.getPatch().getProgram());
			//MidiSystem.getSynthesizer().loadInstrument(instr);
			new MyMidi3().playAndStop(
				new Player()
					.instrument(instr.getPatch().getProgram())
					.setBPM(70)
					.r4()
					.octave(4)
					.p(4, A-12, D_, E)
					.p(4, B-12, D, F)
					.r4()
			);
		} else {
			System.out.println("Next time give me one of the names and I'll play it.");
		}
    }
}
