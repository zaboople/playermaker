package test.hear;
import java.util.HashMap;
import java.util.Map;
import org.tmotte.pm.MyMidi3;

public class XTester {


    public static void main(String[] args){
        MyMidi3 midi=new MyMidi3();
        test(midi,
            new TestArpeggio(),
            new TestBeatWithSound(),
            new TestBends(),
            new TestBends2(),
            new TestBendsPartial(),
            new TestBendTo(),
            new TestChannelTrack(),
            new TestChords(),
            new TestFinish(),
            new TestPlayAfter(),
            new TestReverb(),
            new TestTies(),
            new TestVibrato(),
            new TestVibratoChanging(),
            new TestVibratoSpeed(),
            new TestVibratoTriplet()
        );
        System.out.println("ALL TESTS COMPLETE.");
    }


    private static void test(MyMidi3 midi, XTest... testCalls) {
        for (int i=0; i<testCalls.length; i++) {
            if (i>0) midi.reset();
            XTest call=testCalls[i];
            System.out.println("\nTesting: "+call.getClass().getName());
            call.test(midi, i==testCalls.length-1);
            System.out.println("Test done.\n");
        }
    }

}