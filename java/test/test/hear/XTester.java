package test.hear;
import java.util.HashMap;
import java.util.Map;
import org.tmotte.pm.MyMidi3;

public class XTester {


    public static void main(String[] args){
        try (MyMidi3 midi=new MyMidi3()) {
            if (args.length>0 && args[0].startsWith("-b"))
                test(
                    midi
                    ,new TestBends()
                    ,new TestBends2()
                    ,new TestBendsPartial()
                    ,new TestBendTo()
                );
            else
            if (args.length>0 && args[0].startsWith("-v"))
                test(
                    midi
                    ,new TestVibrato()
                    ,new TestVibratoChanging()
                    ,new TestVibratoSpeed()
                    ,new TestVibratoTriplet()
                );
            else
                test(midi,
                    new Test7Slash16Time()
                    ,new TestArpeggio()
                    ,new TestBeatWithSound()
                    ,new TestBends()
                    ,new TestBends2()
                    ,new TestBendsPartial()
                    ,new TestBendTie()
                    ,new TestBendTo()
                    ,new TestBPM()
                    ,new TestChannelTrack()
                    ,new TestChords()
                    ,new TestDelay()
                    ,new TestDrums()
                    ,new TestFinish()
                    ,new TestPlayAfter()
                    ,new TestReverb()
                    ,new TestTies()
                    ,new TestTies2()
                    ,new TestVibrato()
                    ,new TestVibratoChanging()
                    ,new TestVibratoSpeed()
                    ,new TestVibratoTriplet()
                    ,new TestVolume()
                    ,new TestVolumeSwell()
                );
            System.out.println("ALL TESTS COMPLETE.");
        }
    }


    private static void test(MyMidi3 midi, XTest... testCalls) {
        for (int i=0; i<testCalls.length; i++) {
            if (i>0) midi.reset();
            XTest call=testCalls[i];
            System.out.println("Testing: "+call.getClass().getName());
            call.test(midi, false);
            System.out.println("Test done.");
        }
        midi.close();
    }

}