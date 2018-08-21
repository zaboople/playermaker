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
				.setBPM(70)
				.setReverb(127)
				.r4()
				.octave(3)

				// 1
				.p(4, F, B_, F+12)
				.r(8.)

				// 2
				.p(4, F, B_, F+12, A+12, C+24)
				.r(8.)

				// 3
				.p(4, A+12, C+24, E+24)
				.p(16, G+24)
				.p(16, F+24)
				.p(16, E+24)

				// 4
				.p(4, F, B_, F+12, A+12, C+12)
				.p(16, D+24)
				.p(16, E_+24)
				.c(16, E+12)

				// 1
					.t(4)
					.r(16)
					.c(4, F, B_+12, C+24, E+24)
					.up()
				.p(8., G+24)

				// 2
				.p(4, F, B_, F+12, A+12, C+24)
				.p(8, F+24)
				.c(16, F, B_+24)

				// 3
					.t(4)
					.r(8)
					.c(4, A+12, C+24, E+24)
					.up()
				.r(8)
				.c(16, F, A+24)

				// 4
					.t(4)
					.r(16)
					.c(4, F, B_, F+12, A+12, C+12)
					.up()
				.p(8, E+24)
				.p(16, F+24)

				// 1
				.p(8, F)
				.p(8, E)
				.p(8, A+12)
				.p(8, C+12)
				.p(8, E, F+24, C+24)
				.p(8, F, E+24, A+24)
				.c(8, F, B_, F+12, A+12, C+24)

				// 2
					.t(4)
					.up()
				.c(8., F, B_)

				// 3
					.t(4)
					.up()
				.p(8., C, E, A+12)

				// 4
				.p(4, F, A, C+12, F+12)
				.c(8., F, A, C+12, F+12)

				// 1
					.t(4)
					.t(8.)
					.up()

				.r2()
		);
    }
}
