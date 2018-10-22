package org.tmotte.pm;
import java.util.Arrays;
import org.tmotte.common.text.Log;

public class SwellGenTest {
    // Note to self: Swell(delay, duration, toVolume)

    public static void main(String[] args) {
        Log.add("SwellGen", "MyMidi3", "MidiTracker");
        test4();
        //test1();
        //test2();
    }
    private static void print(int channel, int volume, long tick) {
        if (volume > 127 || volume <0)
            throw new RuntimeException("Illegal: "+volume);
        System.out.append("[test] Volume ").append(String.valueOf(volume)).append(" tick ").append(String.valueOf(tick)).append("\n");
    }
    private static void test1() {
        new SwellGen(()->6, SwellGenTest::print).handle(
            0, 0,
            10,
            Arrays.asList(new Swell(0, 100, 50))
        );
    }
    private static void test2() {
        new SwellGen(()->6, SwellGenTest::print).handle(
            0, 0,
            63,
            Arrays.asList(new Swell(5, 100, 0))
        );
    }

    private static void test3() {
        new SwellGen(()->6, SwellGenTest::print).handle(
            0, 0,
            127,
            Arrays.asList(new Swell(5, 100, 0))
        );
    }
    private static void test4() {
        new SwellGen(()->6, SwellGenTest::print).handle(
            0, 0,
            63,
            Arrays.asList(new Swell(5, 100, 127), new Swell(0, 100, 0))
        );
    }

    private static void test5() {
        new SwellGen(()->6, SwellGenTest::print).handle(
            0, 0,
            63,
            Arrays.asList(new Swell(5, 100, 65), new Swell(0, 100, 68))
        );
    }

}