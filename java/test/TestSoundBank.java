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
import org.tmotte.pm.Divisions;
import org.tmotte.pm.MyMidi3;
import org.tmotte.pm.Player;
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
		Player player=new Player();
		instrName.ifPresent(i -> player.instrument(i));
		if (!instrName.isPresent())
			player.instrument(0);
		midi.playAndStop(
			player
				.setBPM(60)
				.setReverb(127)
				.r4()
				.octave(3)
				.p(4, F, B_, F+12)
				.r(8)
				.p(4, F, B_, F+12, A+12, C+24)
				.r(8)
				.p(4, A+12, C+24, E+24, E+36, C+48)
				.r(8)
				.p(4, F, B_, F+12, A+12, C+12)
				.r4()
		);
    }
}
