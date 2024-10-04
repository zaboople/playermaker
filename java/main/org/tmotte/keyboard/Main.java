package org.tmotte.keyboard;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.io.File;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;

/** Runs the keyboard */
public class Main {

	/** Provides command-line entry
	 * @param args Use -help for options
	 */
    public static void main(String[] args) {
	    System.out.println("Starting up...");
        Optional<File>
            instrumentFile=Optional.empty(),
            sequenceFile=Optional.empty();
        for (int i=0; i<args.length; i++)
            if (args[i].startsWith("-i"))
                instrumentFile=getFile(args[++i]);
            else
            if (args[i].startsWith("-s"))
                sequenceFile=getFile(args[++i]);
            else
            if (args[i].startsWith("-h")) {
	            System.out.println("""
	            """);
            } else {
                System.err.println("Unexpected: "+args[i]);
                System.exit(1);
                return;
            }
        SynthWrapper synthWrapper=new SynthWrapper().init(instrumentFile, sequenceFile);
        PanelMain.startApplication(synthWrapper);
    }
    private static Optional<File> getFile(String name) {
        File f=new File(name);
        if (!f.exists()) {
            System.err.println("No such file: "+f);
            System.exit(1);
            return Optional.empty();
        }
        return Optional.of(f);
    }
    private Main(){}
}
