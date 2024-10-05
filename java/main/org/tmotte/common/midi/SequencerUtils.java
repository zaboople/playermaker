package org.tmotte.common.midi;
import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.sound.midi.Receiver;
import org.tmotte.common.function.Except;

/** Utilities for doing some unusually funky things with sequencers and synths */
public class SequencerUtils  {
	private SequencerUtils(){}

	/**
	 * The sequencer is not necessarily using the same synthesizer as you'd expect. But
	 * more importantly, there is a BIG difference between these two:
	   <ul>
	   <li>Sequencer.getTransmitters(): Gets existing transmitters, if there are any
	   <li>Sequencer.getTransmitter(): CREATES a new transmitter, even if one or more
	       already exist.
	   </ul>
	 * The API works this way so that a sequencer can transmit the same thing to multiple resources;
	 * the problem is that getTransmitter() should be renamed createTransmitter().
	 * @param sequencer The sequencer that generates music
	 * @param synthesizer The synthesizer that plays the music
	 */
	public static void hookSequencerToSynth(Sequencer sequencer, Synthesizer synthesizer) {
		for (Transmitter t: sequencer.getTransmitters())
			Optional.ofNullable(t.getReceiver()).ifPresent(Receiver::close);
		sequencer.getTransmitters().stream()
	        .findFirst()
	        .orElse(Except.get(()->sequencer.getTransmitter()))
	        .setReceiver(Except.get(()->synthesizer.getReceiver()));
    }

	/** This is of questionable virtue. I haven't actually tried loading instruments yet.
		@param synth Instruments will be unloaded if replacementFile has any
		@param replacementFile Instruments to be used instead
	*/
    public static Instrument[] getOrReplaceInstruments(Synthesizer synth, Optional<File> replacementFile) {
		return replacementFile
			.map(file ->
				Except.get(()->{
					synth.unloadAllInstruments(synth.getDefaultSoundbank());
					Soundbank soundbank=MidiSystem.getSoundbank(file);
					//synth.loadAllInstruments(soundbank);
					return soundbank.getInstruments();
				})
			).orElseGet(()->
				synth.getDefaultSoundbank().getInstruments()
			);
    }
}
