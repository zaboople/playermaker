package test;
import java.io.File;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;


/**
 */
public class TestMidiFile  {
    public static void main(String args[]) throws Exception {
	    if (args.length==0) throw new RuntimeException("Needs a file input");
	    File file=new File(args[0]);
		Sequence sequence=MidiSystem.getSequence(file);
		Track[] tracks=sequence.getTracks();
		for (Track track: tracks) {
			System.out.println("Track "+track);
			for (int i=0; i<track.size(); i++) {
				MidiEvent event=track.get(i);
				System.out.println("Tick "+event.getTick());
				MidiMessage message=event.getMessage();
				byte[] bytes=message.getMessage();
				int status=message.getStatus();
				System.out.print("Message status: "+status+" bytes: " );
				for (byte b: bytes) {
					int ib=b & 0xFF;
					System.out.append(String.valueOf(ib)).append(" ");
				}
				switch (status) {
					case 144: System.out.append("NOTE ON"); break;
					case 128: System.out.append("NOTE OFF"); break;
					case 176: System.out.append("CTRL CHG"); break;
					case 192: System.out.append("PROGRAM"); break;
					default: System.out.append("?");
				}
				System.out.println();
			}
		}
		for (int i=0; i<256; i++) {
			byte b=(byte)i;
			System.out.append(b+" ");
		}

	}
}
