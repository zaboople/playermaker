package org.tmotte.pm;
import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Optional;

/**
 * FIXME name MyMidiSequencer
 * This is really intended to support a group of Player objects who should be
 * passed to sequence() all at once.
 */
public class MyMidi3  {
    int transpose = 24;

    // The latter is inversely related to the other:
    public final static int SEQUENCE_RESOLUTION=Divisions.whole * 5 * 7;

    // This isn't really used, but it's just to signify that the number
    // of ticks in a second is twice the resolution, no matter what
    // resolution you give:
    public final static int TICKS_PER_SECOND=SEQUENCE_RESOLUTION*2;
    public final static int TICKS_PER_MINUTE=TICKS_PER_SECOND*60;

    final static int PROGRAM = 192; //FIXME these are already in SHORTMESSAGE
    final static int NOTEON = 144;
    final static int NOTEOFF = 128;
    final static int SUSTAIN = 64;
    final static int REVERB = 91;
    final static int BEND = 224;
    final static int SEQUENCER_END_PLAY=47;
    final static int C=0, D=2, E=4, F=5, G=7, A=9, B=11;

    private Sequencer sequencer;
    private boolean waitForSequencerToStopPlaying=true;
    private Sequence sequence;
    private ReserveChannels reservedChannels=new ReserveChannels();

    private Track currTrack;
    private int currChannelIndex=0;
    public long tickX=0;

    public MyMidi3() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
            setBeatsPerMinute(60);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Currently the quarter note always gets 1 beat */
    public MyMidi3 setBeatsPerMinute(int bpm) {
        tickX = Math.round(
            ((double)TICKS_PER_MINUTE) /
            (
                ((double)bpm) * ((double) Divisions.reg4)
            )
        );
        return this;
    }

    public MyMidi3 reset() {
        try {
            sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public void write(File file) throws Exception {
        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
        if (MidiSystem.write(sequence, fileTypes[0], file) == -1) {
            throw new Exception("Write didn't work");
        }
    }


    /** FIXME players should go in constructor maybe. */
    public MyMidi3 sequence(Player... players) throws Exception {
        for (Player player: players)
            reservedChannels.reserve(player);

        for (Player player: players) {

            // Tracks & channel setup:
            currTrack=sequence.createTrack();;
            currChannelIndex=player.channelIndex;

            // Do time:
            long currTick=player.startTime * tickX;

            // Setup of instrument:
            setupChannelForPlayer(player, currTick);
            int instrument=player.instrumentIndex;

            // And blast off the rocket:
            for (Chord sound: player.sounds()) {
                if (sound.instrument!=instrument) {
                    instrument=sound.instrument;
                    sendInstrument(instrument, currTick);
                }

                int spareTrackIndex=-1;
                long soundStart=currTick;
                for (Note note: sound.notes()) {

                    // Local variables for note attributes:
                    TonalAttributes attrs=note.attrs;
                    int pitch=note.pitch+attrs.transpose,
                        volume=note.attrs.volume;
                    long restBefore=note.restBefore * tickX;
                    List<Bend> noteBends=note.bends();

                    // Note start time:
                    currTick=soundStart + restBefore;

                    // Send any bends, and the note start:
                    if (!noteBends.isEmpty()) {
                        reservedChannels.useSpare(player, currTick);
                        System.out.println("Using spare: "+currChannelIndex);
                        sendBends(soundStart + restBefore, noteBends);
                    }
                    //System.out.println("Pitch "+pitch+" at "+currTick);
                    event(NOTEON, pitch, volume, currTick);


                    // Finish the note:
                    currTick += note.duration * tickX;
                    event(NOTEOFF, pitch, volume, currTick);
                    if (!noteBends.isEmpty())
                        eventBendEnd(currTick);

                    // Get back on channel if we had to use a reserve:
                    currChannelIndex=player.channelIndex;
                }

                // Finish up with the chord bends (if any) and
                // and advance the currTick counter:
                if (!sound.bends().isEmpty())
                    sendBends(soundStart, sound.bends());
                currTick=soundStart+(sound.totalDuration() * tickX);
                if (!sound.bends().isEmpty())
                    eventBendEnd(currTick);
            }
        }
        return this;
    }

    private void setupChannelForPlayer(Player player, long tick) throws Exception {
        // Reverb doesn't work for me. Bummer.
        /*
        System.out.println("REVERB "+currChannelIndex+" "+currTrack+" "+player.getReverb());
        System.out.println("setupChannel "+currChannelIndex+" tick "+tick
            +" bend sense "+player.getBendSensitivity()+" "+player.instrumentIndex);
        */
        sendBendSensitivity(player.getBendSensitivity(), tick);
        sendReverb(player.getReverb(), tick);
        sendInstrument(player.instrumentIndex, tick);
    }

    /** One instance exists within the main class; ReserveChannels has hooks going back out to currChannelIndex
        and currTrack. Sort of ugly. */
    private class ReserveChannels {
        boolean[] reservedAll=new boolean[16];
        Map<Player, Set<Integer>> playerSetupAlready=new HashMap<>();

        /** Players can overlap on a channel, assuming it won't cause a problem. */
        public void reserve(Player player) {
            reservedAll[player.channelIndex]=true;
        }
        public void useSpare(Player player, long tick) throws Exception {
            if (!reservedAll[currChannelIndex+1]) {
                currChannelIndex+=1;
                Set<Integer> already=playerSetupAlready.computeIfAbsent(player, p -> new HashSet<>());
                if (!already.contains(currChannelIndex)) {
                    System.out.println("Setting up spare...");
                    already.add(currChannelIndex);
                    setupChannelForPlayer(player, tick);
                }
            }
            else
                throw new RuntimeException(
                    "Ran out of spare channels after "+currChannelIndex+"; "
                   +"Make sure to give enough space between the channels of different players, "
                   +"or put players on the same channel when you know they can't interfere with "
                   +"each other. Spare channels are needed when individual Notes are bent - as "
                   +"opposed to bending entire Chords"
                );
        }
    }

    private void sendBends(long soundStart, List<Bend> bends) {
        System.out.println("MyMidi3: Bends "+bends.size());
        final int max=8192;
        long t=soundStart;
        int pitch=max;//fixme is that correct?
        bender.init(soundStart);
        for (Bend bend: bends)
            bender.send(bend.delay, bend.duration, bend.denominator);
    }

    final Bender bender=new Bender();
    private class Bender {
        //FIXME put bend back by itself
        int pitch;
        long t;
        public void init(long soundStart) {
            this.t=soundStart;
            this.pitch=8192;//FIXME MAKE CONSTANT
        }
        public void send(long delay, long duration, int denominator) {
            //System.out.println("Bend delay "+delay+" duration "+duration+" denominator "+denominator);
            final int max=8192; //FIXME bad name and make constant
            t+=(delay * tickX);
            int change=max/denominator;
            int perTicky = change / (int)duration;
            int leftover = change % (int)duration;
            int leftoverIncr=leftover>0 ?1 :-1;
            for (int i=0; i<duration; i++) {
                int thisAmount = perTicky;
                if (leftover!=0) {
                    thisAmount+=leftoverIncr;
                    leftover-=leftoverIncr;
                }
                pitch+=thisAmount;
                if (pitch==16384) pitch=16383;
                //System.out.println("Sending bend "+pitch+" at "+t);
                eventBend(pitch, t);
                t+=tickX;
            }
        }
    }

    private void eventBend(int amount, long tick) {
        // A bend is 14 bits - no, not 16. That won't fit in a byte, but
        // even better, you are required to split it into a 7-bits-each pair.
        int lsb=amount & 127,
            msb=amount >>> 7;
        event(BEND, lsb, msb, tick);
    }
    private void eventBendEnd(long tick) {
        eventBend(8192, tick);
    }


    private void sendInstrument(int instrumentIndex, long tick) {
        event(PROGRAM, instrumentIndex, 0, tick);
    }

    private void event(int type, int firstNum, int secondNum, long tick) {
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(type + currChannelIndex, firstNum, secondNum);//FIXME can't we do this in one shot
            sendMessage(message, tick);//FIXME can't we handle exceptions in one place
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROL CHANGE:                                                                        //
    // https://www.midi.org/specifications/item/table-3-control-change-messages-data-bytes-2  //
    ////////////////////////////////////////////////////////////////////////////////////////////

    private void sendBendSensitivity(int amount, long tick) throws Exception {
        //This type of message is command, channel, data1, data2
        //RPN MSB (always 0)
        sendMessage(
            new ShortMessage(
                ShortMessage.CONTROL_CHANGE, currChannelIndex, 101, 0
            ),
            tick
        );
        //RPN LSB (for pitch sensitivity, 0)
        sendMessage(
            new ShortMessage(
                ShortMessage.CONTROL_CHANGE, currChannelIndex, 100, 0
            ),
            tick
        );
        //Data Entry MSB
        sendMessage(
            new ShortMessage(
                ShortMessage.CONTROL_CHANGE, currChannelIndex, 6, amount
            ),
            tick
        );
        //Data Entry LSB:
        sendMessage(
            new ShortMessage(
                ShortMessage.CONTROL_CHANGE, currChannelIndex, 38, 0
            ),
            tick
        );
    }

    /** I think the reverb limit is 127, oh and this appears broken */
    private void sendReverb(int amount, long tick) throws Exception {
        //System.out.println("Sent reverb "+amount);
        sendMessage(
            new ShortMessage(
                ShortMessage.CONTROL_CHANGE, currChannelIndex, 92, amount
            ),
            tick
        );
    }

    private void sendMessage(ShortMessage msg, long tick) {
        currTrack.add(new MidiEvent(msg, tick));
    }



    ///////////
    // PLAY: //
    ///////////

    private static class MyNotifier extends ArrayBlockingQueue<Integer> {
        public MyNotifier() {super(1);}
        void give() {add(1);}
        void grab() {
            try {take();} catch (Exception e) {throw new RuntimeException(e);}
        }
    }
    public MyMidi3 play() {
        return play(false);
    }
    public MyMidi3 playAndStop() {
        return play(true);
    }
    public MyMidi3 play(Player... players) throws Exception {
        sequence(players);
        return play(false);
    }
    public void playAndStop(Player... players) throws Exception {
        sequence(players);
        play(true);
    }
    public MyMidi3 play(boolean andThenStop) {
        try {
            if (!sequencer.isOpen())
                sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(0);
            sequencer.setLoopStartPoint(0);
            sequencer.setTickPosition(0);
            Optional<MyNotifier> notifier= waitForSequencerToStopPlaying
                ?Optional.of(new MyNotifier())
                :Optional.empty();
            sequencer.addMetaEventListener(
                event ->{
                    if (event.getType() == SEQUENCER_END_PLAY){
                        if (andThenStop)
                            close();
                        notifier.ifPresent(MyNotifier::give);
                    }
                }
            );
            sequencer.start();
            notifier.ifPresent(MyNotifier::grab);
        } catch (Exception e) {
            sequencer.close();
            throw new RuntimeException(e);
        }
        return this;
    }

    public void stopPlay() {
        sequencer.stop();
    }
    public void close() {
        sequencer.close();
        sequencer=null;
    }

}
