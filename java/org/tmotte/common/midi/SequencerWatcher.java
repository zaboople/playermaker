package org.tmotte.common.midi;
import javax.sound.midi.Sequencer;
import java.util.concurrent.ArrayBlockingQueue;
import org.tmotte.common.function.Except;

/**
 * This wrapper for Sequencer allows me to put in a shutdown hook and wait for
 * the sequencer to stop playing; normally it just goes off in its own thread.
 */
public class SequencerWatcher {
    private final static int SEQUENCER_END_PLAY=47;
    private final ArrayBlockingQueue<Integer> eventHook=new ArrayBlockingQueue<>(1);

    private boolean waitForEndPlay=true, closeOnEndPlay=false;

    public SequencerWatcher(Sequencer sequencer) {
        sequencer.addMetaEventListener(
            event ->{
                if (event.getType() == SEQUENCER_END_PLAY){
                    if (closeOnEndPlay)
                        sequencer.close();
                    if (waitForEndPlay)
                        eventHook.add(1);
                }
            }
        );
    }
    /** Defaults to false */
    public SequencerWatcher closeOnFinishPlay(boolean close) {
        this.closeOnEndPlay=close;
        return this;
    }
    /** Defaults to true */
    public SequencerWatcher waitForFinishPlay(boolean wait) {
        this.waitForEndPlay=wait;
        return this;
    }
    public void waitForIf() {
        if (waitForEndPlay)
            waitForFinish();
    }
    /** This waits for the sequencer to stop playing. */
    public void waitForFinish() {
        Except.run(()->eventHook.take());
    }
}
