package org.tmotte.common.midi;
import java.io.Closeable;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import org.tmotte.common.function.Except;

/**
 * This abandoned wrapper for sequencer/synthesizer is superseded by MyMidi3.
 */
class SequencerMgr implements Closeable {
    private final static int SEQUENCER_END_PLAY=47;
    private final ArrayBlockingQueue<Integer> endPlayHook=new ArrayBlockingQueue<>(1);
    private boolean async=false;

    private Sequencer sequencer;
    private Synthesizer synth;
    private Instrument[] instruments;
    private Map<String, MetaInstrument> instrumentsByName;
    private final MidiTracker midiTracker=new MidiTracker();

    SequencerMgr() {
        this(Optional.empty(), Optional.empty(), Optional.empty());
    }

    SequencerMgr(Optional<Synthesizer> synthOpt, Optional<Sequencer> sequencerOpt, Optional<File> replaceInstrumentsWith) {
        Except.run(()-> {
            synth=synthOpt.orElse(MidiSystem.getSynthesizer());
            synth.open();
            setInstruments(
                SequencerUtils.getOrReplaceInstruments(synth, replaceInstrumentsWith)
            );
            sequencer=sequencerOpt.orElse(MidiSystem.getSequencer());
            SequencerUtils.hookSequencerToSynth(sequencer, synth);
        });
        sequencer.addMetaEventListener(
            event ->{
                if (event.getType() == SEQUENCER_END_PLAY && !async){
                    endPlayHook.add(1);
                }
            }
        );
    }


    SequencerMgr play(Sequence seq) {
        Except.run(() -> {
            //System.out.println("SequencerMgr.play() starting..."+sequencer+" "+andThenStop);
            if (!sequencer.isOpen())
                sequencer.open();
            sequencer.setSequence(seq);
            sequencer.setLoopCount(0);
            sequencer.setLoopStartPoint(0);
            sequencer.setTickPosition(0);
            sequencer.start();
            if (!async)
                endPlayHook.take();
        });
        return this;
    }

    private void setInstruments(Instrument... instruments) {
        this.instruments=instruments;
        instrumentsByName=MetaInstrument.map(true, instruments);
    }

    Instrument[] getInstruments() {
        return this.instruments;
    }

    Instrument getInstrument(String name) {
        return instrumentsByName.get(name).instrument;
    }

    Sequencer getSequencer() {
        return sequencer;
    }


    /** Defaults to false
        @param async To async or not
        @return this
     */
    SequencerMgr setAsync(boolean async) {
        this.async=async;
        return this;
    }

    public @Override void close() {
        sequencer.close();
    }
}
