package org.tmotte.pm;
import org.tmotte.common.function.Except;
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
import org.tmotte.keyboard.MetaChannel;

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

    private MySequencer sequencer;
    private Sequence sequence;
    private MetaChannel[] metaChannels;
    private Instrument[] instruments;
    private Track currTrack;

    private boolean waitForSequencerToStopPlaying=true;
    private ReserveChannels reservedChannels=new ReserveChannels();
    private Map<String, MetaInstrument> instrumentsByName;

    private int currChannelIndex=0;
    public long tickX=0;

    /**
     * This wrapper for Sequencer allows me to put in a shutdown hook
     */
    static class MySequencer {
        private final ArrayBlockingQueue<Integer> eventHook=new ArrayBlockingQueue<>(1);

        boolean waitForEndPlay=true, closeOnEndPlay=false;
        Sequencer realSequencer;

        public MySequencer(Synthesizer synth) {
            Except.run(()-> {
                realSequencer=MidiSystem.getSequencer();
                for (Transmitter t: realSequencer.getTransmitters())
                    Optional.ofNullable(t.getReceiver()).ifPresent(Receiver::close);
                realSequencer.getTransmitters().stream()
                    .findFirst()
                    .orElse(realSequencer.getTransmitter())
                    .setReceiver(synth.getReceiver());
            });
            realSequencer.addMetaEventListener(
                event ->{
                    if (event.getType() == SEQUENCER_END_PLAY){
                        if (closeOnEndPlay)
                            realSequencer.close();
                        if (waitForEndPlay)
                            eventHook.add(1);
                    }
                }
            );
        }
        void waitForIf() {
            if (waitForEndPlay)
                Except.run(()->eventHook.take());
        }
    }

    public MyMidi3() {
        Except.run(()-> {
            Synthesizer synth=MidiSystem.getSynthesizer();
            synth.open();
            this.instruments=synth.getDefaultSoundbank().getInstruments();
            instrumentsByName=MetaInstrument.map(instruments);
            sequencer = new MySequencer(synth);
            sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
            setBeatsPerMinute(60);
            metaChannels=MetaChannel.getChannels(synth);
        });
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

    public MyMidi3 setReverb(int reverb) {
        for (MetaChannel mc: metaChannels)
            mc.setReverb(reverb);
        return this;
    }

    public MyMidi3 setReverb(int reverb, int channel) {
        metaChannels[channel].setReverb(reverb);
        return this;
    }

    public MyMidi3 reset() {
        Except.run(()-> {
            sequence = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);
        });
        return this;
    }

    public void write(File file) throws Exception {
        int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
        if (MidiSystem.write(sequence, fileTypes[0], file) == -1) {
            throw new Exception("Write didn't work");
        }
    }


    public MyMidi3 sequence(Player... players) {
        return Except.get(()-> sequenceMessy(players));
    }
    private MyMidi3 sequenceMessy(Player... players) throws Exception {
        for (Player player: players)
            reservedChannels.reserve(player);
        for (Player player: players)
            sequencePlayer(player);
        return this;
    }

    private void sequencePlayer(Player player) throws Exception {
        // Track & time & defaults:
        currTrack=sequence.createTrack();;
        long currTick=player.startTime * tickX;
        int bendSense=8192;

        // And blast off the rocket:
        for (Event event: player.events()){
            {
                Integer eBpm=event.getBeatsPerMinute(),
                    eBendSense=event.getBendSensitivity(),
                    eChannel=event.getChannel();
                if (eBpm!=null)       setBeatsPerMinute(eBpm);
                if (eChannel!=null)   currChannelIndex=eChannel;
                if (eBendSense!=null) sendBendSense(bendSense=eBendSense, currTick);
            }

            getInstrument(event, currTick);

            Chord chord=event.getChord();
            if (chord!=null) {
                long soundStart=currTick;
                int wasChannel=currChannelIndex;
                for (Note note: chord.notes()) {

                    // Local variables for note attributes:
                    int pitch=note.pitch+note.getTranspose(),
                        volume=note.getVolume();
                    long restBefore=note.restBefore * tickX;
                    List<Bend> noteBends=note.bends();

                    // Note start time:
                    currTick=soundStart + restBefore;

                    // Send any bends, and the note start:
                    if (!noteBends.isEmpty()) {
                        reservedChannels.useSpare(player, bendSense, currTick);
                        System.out.println("Using spare: "+currChannelIndex);
                        sendBends(soundStart + restBefore, noteBends);
                    }
                    if (pitch<0)
                        throw new RuntimeException("Invalid pitch "+pitch+" from player "+player);
                    event(NOTEON, pitch, volume, currTick);


                    // Finish the note:
                    currTick += note.duration * tickX;
                    event(NOTEOFF, pitch, volume, currTick);
                    if (!noteBends.isEmpty())
                        eventBendEnd(currTick);

                    // Get back on channel if we had to use a reserve:
                    currChannelIndex=wasChannel;
                }

                // Finish up with the chord bends (if any) and
                // and advance the currTick counter:
                if (!chord.bends().isEmpty())
                    sendBends(soundStart, chord.bends());
                currTick=soundStart+(chord.totalDuration() * tickX);
                if (!chord.bends().isEmpty())
                    eventBendEnd(currTick);
            }
        }
    }

    private void getInstrument(Event event, long tick) {
        Instrument instrument=event.getInstrument();
        String instrumentName=event.getInstrumentName();
        if (instrumentName!=null) {
            MetaInstrument mi=instrumentsByName.get(instrumentName);
            if (mi==null) throw new IllegalStateException("No instrument named: \""+instrumentName+"\"");
            instrument=mi.instrument;
        }
        Integer instrumentIndex=event.getInstrumentIndex();
        if (instrumentIndex!=null)
            instrument=instruments[instrumentIndex];
        // Finally send it:
        if (instrument!=null)
            sendInstrument(currChannelIndex, instrument, tick);
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
        public void useSpare(Player player, int bendSense, long tick) throws Exception {
            if (!reservedAll[currChannelIndex+1]) {
                currChannelIndex+=1;
                Set<Integer> already=playerSetupAlready.computeIfAbsent(player, p -> new HashSet<>());
                if (!already.contains(currChannelIndex)) {
                    System.out.println("Setting up spare...");
                    already.add(currChannelIndex);
                    sendBendSense(bendSense, tick);
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
        //System.out.println("MyMidi3: Bends "+bends.size());
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

	private void sendInstrument(int channel, Instrument instr, long tick) {
		Except.run(()-> {
            Patch patch = instr.getPatch();
			int bank = patch.getBank(),
				program=patch.getProgram();
			int msgType = ShortMessage.CONTROL_CHANGE;
	        sendMessage(
		        new ShortMessage(msgType, channel, 0,  bank >> 7), tick   // = 9
	        );
	        sendMessage(
		        new ShortMessage(msgType, channel, 32, bank & 0x7f), tick // = 0
	        );
	        event(PROGRAM, program, 0, tick);
        });
	}


    private void event(int type, int firstNum, int secondNum, long tick) {
        Except.run(()-> {
            ShortMessage message = new ShortMessage();
            message.setMessage(type + currChannelIndex, firstNum, secondNum);//FIXME can't we do this in one shot
            sendMessage(message, tick);
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROL CHANGE:                                                                        //
    // https://www.midi.org/specifications/item/table-3-control-change-messages-data-bytes-2  //
    ////////////////////////////////////////////////////////////////////////////////////////////

    /** Short for Bend Sensitivity (hard word to type). */
    private void sendBendSense(int amount, long tick) throws Exception {
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

    private void sendMessage(ShortMessage msg, long tick) {
        currTrack.add(new MidiEvent(msg, tick));
    }



    ///////////
    // PLAY: //
    ///////////


    public MyMidi3 play() {
        return play(false);
    }
    public MyMidi3 playAndStop() {
        return play(true);
    }
    public MyMidi3 play(boolean stop, Player... players) {
        sequence(players);
        return play(stop);
    }
    public void playAndStop(Player... players)  {
        sequence(players);
        play(true);
    }
    public MyMidi3 play(boolean andThenStop) {
        sequencer.closeOnEndPlay=andThenStop;
        Sequencer sqr=sequencer.realSequencer;
        try {
            //System.out.println("MyMidi3.play() starting..."+sequencer+" "+andThenStop);
            if (!sqr.isOpen())
                sqr.open();
            sqr.setSequence(sequence);
            sqr.setLoopCount(0);
            sqr.setLoopStartPoint(0);
            sqr.setTickPosition(0);
            sqr.start();
            sequencer.waitForIf();
        } catch (Exception e) {
            sqr.close();
            throw new RuntimeException(e);
        }
        return this;
    }

    public void stopPlay() {
        sequencer.realSequencer.stop();
    }
    public void close() {
        System.out.println("MyMidi3.close()");
        sequencer.realSequencer.close();
        //sequencer=null;
    }

}
