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
 * This wrapper for Sequencer allows me to put in a shutdown hook and wait for
 * the sequencer to stop playing; normally it just goes off in its own thread.
 */
public class SequencerMgr implements Closeable {
    private final static int SEQUENCER_END_PLAY=47;
    private final ArrayBlockingQueue<Integer> endPlayHook=new ArrayBlockingQueue<>(1);
    private boolean async=false;

    private Sequencer sequencer;
    private Synthesizer synth;
    private MidiChannel[] midiChannels;
    private Instrument[] instruments;
    private final MidiTracker midiTracker=new MidiTracker();
    private Map<String, MetaInstrument> instrumentsByName;

    public SequencerMgr() {
        this(Optional.empty(), Optional.empty(), Optional.empty());
    }

    public SequencerMgr(Optional<Synthesizer> synthOpt, Optional<Sequencer> sequencerOpt, Optional<File> replaceInstrumentsWith) {
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


    public SequencerMgr play(Sequence seq) {
        try {
            //System.out.println("SequencerMgr.play() starting..."+sequencer+" "+andThenStop);
            if (!sequencer.isOpen())
                sequencer.open();
            sequencer.setSequence(seq);
            sequencer.setLoopCount(0);
            sequencer.setLoopStartPoint(0);
            sequencer.setTickPosition(0);
            sequencer.start();
            if (!async)
                Except.run(()->endPlayHook.take());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void setInstruments(Instrument... instruments) {
        this.instruments=instruments;
        instrumentsByName=MetaInstrument.map(true, instruments);
    }

    public Instrument[] getInstruments() {
        return this.instruments;
    }

    public Instrument getInstrument(String name) {
        return instrumentsByName.get(name).instrument;
    }

    public Sequencer getSequencer() {
        return sequencer;
    }


    /** Defaults to true */
    public SequencerMgr setAsync(boolean async) {
        this.async=async;
        return this;
    }


    public @Override void close() {
        sequencer.close();
    }
}
