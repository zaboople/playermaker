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

    private boolean async=false, closeOnEndPlay=false;

    public SequencerWatcher(Sequencer sequencer) {
        sequencer.addMetaEventListener(
            event ->{
                if (event.getType() == SEQUENCER_END_PLAY){
                    if (closeOnEndPlay)
                        sequencer.close();
                    if (!async && eventHook.size()==0)
                        eventHook.add(1);
                }
            }
        );
    }

    /** Defaults to false; if true we will close the underlying Sequencer on
        @param close Whether to close the sequencer when play ends.
        @return this
    */
    public SequencerWatcher closeOnFinishPlay(boolean close) {
        this.closeOnEndPlay=close;
        return this;
    }


    /** This waits for the sequencer to stop playing. */
    public void waitForFinish() {
        if (async)
            throw new IllegalStateException("Not set to synchronous");
        Except.run(()->eventHook.take());
    }

    public SequencerWatcher setAsync(boolean async) {
        this.async=async;
        return this;
    }

}
